package io.github.jhdcruz.memo.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.jhdcruz.memo.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.RuntimeException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthenticationRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val crash: FirebaseCrashlytics,
) : AuthenticationRepository {

    /**
     * Manual sign-in option using input fields,
     */
    override suspend fun manualSignIn(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: FirebaseAuthException) {
            Log.e("Authentication", "Failed to sign in with email and password", e)
            false
        }
    }

    /**
     * Manual Google sign-in/up option
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun googleSignIn(context: Context, reqId: Int): BeginSignInResult {

        val oneTapClient: SignInClient = Identity.getSignInClient(context.applicationContext)

        // Sign-in request config
        val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.GCP_WEB_CLIENT)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()

        val signInResult: BeginSignInResult = suspendCancellableCoroutine { continuation ->
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    continuation.resume(result)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }

        return signInResult
    }

    /**
     * Uses the CredentialManager to sign-in using saved credentials.
     *
     * Also handles Google sign-in/register
     */
    override suspend fun signIn(context: Context): Boolean {
        val credentialManager = CredentialManager.create(context)

        // Options for signing-in
        val passwordOption = GetPasswordOption()
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .setServerClientId(BuildConfig.GCP_WEB_CLIENT)
            .build()

        val getCredRequest = GetCredentialRequest(
            listOf(passwordOption, googleIdOption)
        )

        return withContext(Dispatchers.IO) {
            try {
                // get saved credentials from the user's device
                val result = credentialManager.getCredential(
                    context = context,
                    request = getCredRequest
                )

                handleSignIn(result)
                true
            } catch (e: GetCredentialException) {
                throw e
            } catch (e: FirebaseAuthException) {
                throw e
            } catch (e: IllegalArgumentException) {
                throw e
            }
        }
    }

    override suspend fun signUp(context: Context, email: String, password: String): Boolean {
        val credentialManager = CredentialManager.create(context)

        return try {
            auth.createUserWithEmailAndPassword(email, password).await()

            credentialManager.createCredential(
                context = context,
                request = CreatePasswordRequest(email, password),
            )

            // return true if there is user
            !auth.currentUser?.email.isNullOrEmpty()
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): Boolean {
        // handle authentication based on selected credential
        return when (val credential = result.credential) {
            is PasswordCredential -> {
                val email = credential.id
                val password = credential.password

                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    true
                } catch (e: FirebaseAuthException) {
                    throw e
                }
            }

            is CustomCredential -> {
                // Handles Google sign-in
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // get google token credential
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data).toString()

                        val firebaseCredential =
                            GoogleAuthProvider.getCredential(googleIdTokenCredential, null)

                        auth.signInWithCredential(firebaseCredential)
                        true
                    } catch (e: FirebaseAuthException) {
                        throw e
                    } catch (e: RuntimeException) {
                        throw e
                    }
                } else {
                    false
                }
            }

            else -> {
                // unrecognized credential type.
                Log.e("Authentication", "Unexpected type of credential used.")
                crash.log("Unexpected type of credential used.")
                false
            }
        }
    }
}
