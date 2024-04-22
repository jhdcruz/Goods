package io.github.jhdcruz.memo.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import io.github.jhdcruz.memo.BuildConfig
import io.github.jhdcruz.memo.data.model.User
import io.github.jhdcruz.memo.domain.generateNonce
import io.github.jhdcruz.memo.domain.response.AuthResponseUseCase
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepositoryImpl
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val firestore: FirebaseFirestore,
        private val crashlytics: FirebaseCrashlytics,
    ) : AuthenticationRepository {
        private val nonceSize = 32

        override suspend fun getUser(): User {
            val user = auth.currentUser

            return User(
                uid = user?.uid!!,
                email = user.email,
                name = user.displayName,
                photoUrl = user.photoUrl.toString(),
            )
        }

        /**
         * Save user data to database,
         * also used for updating user data
         * based on changes on Google or Credential Manager.
         *
         * This should be migrated to 'Functions' instead.
         * https://extensions.dev/extensions/rowy/firestore-user-document
         */
        override suspend fun saveUser(user: User): FirestoreResponseUseCase {
            val data =
                User(
                    uid = user.uid,
                    email = user.email,
                    name = user.name,
                    photoUrl = user.photoUrl.toString(),
                )

            return try {
                firestore.collection("users")
                    .document(user.uid)
                    .set(data, SetOptions.merge())
                    .await()

                FirestoreResponseUseCase.Success()
            } catch (e: FirebaseFirestoreException) {
                Log.e("Firestore", "Failed to save new user data", e)
                FirestoreResponseUseCase.Failure(e)
            }
        }

        /**
         * Manual sign-in option using input fields,
         */
        override suspend fun passwordSignIn(
            email: String,
            password: String,
        ): AuthResponseUseCase {
            return try {
                val result = auth.signInWithEmailAndPassword(email, password).await()

                AuthResponseUseCase.Success(result.user!!)
            } catch (e: FirebaseAuthInvalidUserException) {
                Log.i("Authentication", "User not found", e)
                AuthResponseUseCase.NotFound("User not found")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.i("Authentication", "Invalid email or password", e)
                AuthResponseUseCase.Invalid("Invalid email or password")
            } catch (e: FirebaseAuthException) {
                Log.e("Authentication", "Failed to sign in with email and password", e)
                AuthResponseUseCase.Failure(e)
            }
        }

        override suspend fun googleSignIn(context: Context): AuthResponseUseCase {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption: GetGoogleIdOption =
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setAutoSelectEnabled(true)
                    .setNonce(generateNonce(nonceSize))
                    .setServerClientId(BuildConfig.GCP_WEB_CLIENT)
                    .build()

            val getCredRequest =
                GetCredentialRequest(
                    listOf(googleIdOption),
                )

            return try {
                // get saved credentials from the user's device
                val result =
                    credentialManager.getCredential(
                        context = context,
                        request = getCredRequest,
                    )

                handleSignIn(result.credential)
            } catch (e: GetCredentialCancellationException) {
                Log.i("Authentication", "User cancelled sign-in", e)
                AuthResponseUseCase.Cancelled("User cancelled sign-in")
            } catch (e: NoCredentialException) {
                Log.i("Authentication", "No saved credentials found", e)
                AuthResponseUseCase.NotFound("No saved credentials found")
            } catch (e: GetCredentialException) {
                Log.e("Authentication", "Failed to get credential", e)
                AuthResponseUseCase.Error(e)
            } catch (e: FirebaseAuthException) {
                Log.e("Authentication", "Failed to sign in with credential", e)
                AuthResponseUseCase.Failure(e)
            }
        }

        /**
         * Uses the CredentialManager to sign-in using saved credentials.
         *
         * Also handles Google sign-in/register
         */
        override suspend fun signIn(context: Context): AuthResponseUseCase {
            val credentialManager = CredentialManager.create(context)

            // Options for signing-in
            val passwordOption = GetPasswordOption()
            val googleIdOption: GetGoogleIdOption =
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setAutoSelectEnabled(true)
                    .setNonce(generateNonce(nonceSize))
                    .setServerClientId(BuildConfig.GCP_WEB_CLIENT)
                    .build()

            val getCredRequest =
                GetCredentialRequest(
                    listOf(passwordOption, googleIdOption),
                )

            return try {
                // get saved credentials from the user's device
                val result =
                    credentialManager.getCredential(
                        context = context,
                        request = getCredRequest,
                    )

                handleSignIn(result.credential)
            } catch (e: GetCredentialCancellationException) {
                Log.i("Authentication", "User cancelled sign-in", e)
                AuthResponseUseCase.Cancelled("User cancelled sign-in")
            } catch (e: NoCredentialException) {
                Log.i("Authentication", "No saved credentials found", e)
                AuthResponseUseCase.NotFound("No saved credentials found")
            } catch (e: GetCredentialException) {
                Log.e("Authentication", "Failed to start credential manager", e)
                AuthResponseUseCase.Error(e)
            } catch (e: FirebaseAuthException) {
                Log.e("Authentication", "Failed to sign in with credential", e)
                AuthResponseUseCase.Failure(e)
            }
        }

        override suspend fun signUp(
            context: Context,
            email: String,
            password: String,
        ): AuthResponseUseCase {
            val credentialManager = CredentialManager.create(context)

            return try {
                // If the new account was created, the user is also signed in.
                val result = auth.createUserWithEmailAndPassword(email, password).await().user!!
                val user =
                    User(
                        uid = result.uid,
                        email = result.email,
                        name = result.displayName,
                        photoUrl = result.photoUrl.toString(),
                    )

                saveUser(user)

                credentialManager.createCredential(
                    context = context,
                    request = CreatePasswordRequest(email, password),
                )

                AuthResponseUseCase.Success(result)
            } catch (e: FirebaseAuthException) {
                Log.e("Authentication", "Failed to sign up", e)
                AuthResponseUseCase.Failure(e)
            } catch (e: CreateCredentialException) {
                Log.e("Authentication", "Failed to save new credential", e)
                AuthResponseUseCase.Error(e)
            }
        }

        override suspend fun signOut() {
            auth.signOut()
        }

        private suspend fun handleSignIn(credential: Credential): AuthResponseUseCase {
            // handle authentication based on selected credential
            return when (credential) {
                is PasswordCredential -> {
                    val email = credential.id
                    val password = credential.password

                    try {
                        val result = auth.signInWithEmailAndPassword(email, password).await()
                        if (result.user != null) {
                            val user =
                                User(
                                    uid = result.user!!.uid,
                                    email = result.user!!.email,
                                    name = result.user!!.displayName,
                                    photoUrl = result.user!!.photoUrl.toString(),
                                )

                            saveUser(user)
                            AuthResponseUseCase.Success(result.user)
                        } else {
                            AuthResponseUseCase.Invalid("Invalid email or password")
                        }
                    } catch (e: FirebaseAuthException) {
                        Log.e("Authentication", "Failed to sign in with saved password", e)
                        return AuthResponseUseCase.Failure(e)
                    }
                }

                is CustomCredential -> customSignIn(credential)

                else -> {
                    // unrecognized credential type.
                    Log.e("Authentication", "Unexpected type of credential used.")
                    crashlytics.log("Unexpected type of credential used.")

                    return AuthResponseUseCase.Invalid("Unexpected type of credential used.")
                }
            }
        }

        /**
         * Handles third-part login services
         */
        private suspend fun customSignIn(credential: Credential): AuthResponseUseCase {
            return when (credential.type) {
                // Handles Google sign-in
                GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    return try {
                        // get google token credential
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential
                                .createFrom(credential.data)

                        val firebaseCredential =
                            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                        val result = auth.signInWithCredential(firebaseCredential).await()
                        val user =
                            User(
                                uid = result.user!!.uid,
                                email = result.user!!.email,
                                name = result.user!!.displayName,
                                photoUrl = result.user!!.photoUrl.toString(),
                            )

                        saveUser(user)
                        AuthResponseUseCase.Success(result.user)
                    } catch (e: FirebaseAuthException) {
                        Log.e("Authentication", "Failed to sign in with Google credential", e)
                        AuthResponseUseCase.Failure(e)
                    }
                }

                else -> {
                    // unrecognized credential type.
                    Log.e("Authentication", "Unexpected type of custom credential used.")
                    crashlytics.log("Unexpected type of custom credential used.")

                    AuthResponseUseCase.Invalid("Unexpected type of custom credential used.")
                }
            }
        }
    }
