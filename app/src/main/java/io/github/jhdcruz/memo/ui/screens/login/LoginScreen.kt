package io.github.jhdcruz.memo.ui.screens.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.jhdcruz.memo.ui.components.ConfirmDialog
import io.github.jhdcruz.memo.ui.components.GoogleButton
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

/**
 * Combined login and user registration flow,
 *
 * Logging in without existing account will automatically
 * offer to create one.
 *
 * **[context] requires the activity's context.**
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel<LoginViewModelImpl>(),
    context: Context,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val authStatus = viewModel.status.collectAsState(initial = "")

    // Launch sign in flow on initial start
    LaunchedEffect(context) {
        viewModel.onSignIn(context)
    }

    // Show snackbar based on auth status result
    if (authStatus.value.isNotEmpty()) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = authStatus.value,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxHeight()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.weight(1f))

            Text(
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                text = "Let's get you started!"
            )

            Spacer(modifier = Modifier.height(24.dp))

            LoginForm(
                scope = scope,
                viewModel = viewModel,
                modifier = modifier,
                context = context,
            )

            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1F)
                        .padding(vertical = 12.dp)
                )

                Text(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    text = "Or, sign in using"
                )

                HorizontalDivider(
                    modifier = Modifier
                        .weight(1F)
                        .padding(vertical = 12.dp)
                )
            }

            // manual google sign-in if
            // credential manager is not available/preferred
            GoogleButton {
                scope.launch {
                    viewModel.onGoogleSignIn(context)
                }
            }

            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(54.dp),
                onClick = {
                    scope.launch {
                        viewModel.onSignIn(context)
                    }
                }
            ) {
                Text("Login using Credential Manager")
            }

            Spacer(modifier = Modifier.weight(1f))

            ClickableText(
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        append("Copyright © 2024 jhdcruz")
                    }
                },
                onClick = {
                    // open link in external browser
                    val intent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jhdcruz/Memo"))
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun LoginForm(
    viewModel: LoginViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    modifier: Modifier,
    context: Context,
) {
    val notFound = remember { mutableStateOf(false) }
    val email = viewModel.email.collectAsState(initial = "")
    val password = viewModel.password.collectAsState(initial = "")

    val authStatus = viewModel.status.collectAsState(initial = "")

    Column {

        AnimatedVisibility(
            enter = fadeIn(),
            exit = fadeOut(),
            label = "Account Creation Dialog",
            visible = notFound.value
        ) {
            ConfirmDialog(
                onDismissRequest = { notFound.value = false },
                onConfirmation = {
                    scope.launch {
                        viewModel.onSignUp(context, email.value, password.value)
                    }.job.invokeOnCompletion {
                        notFound.value = false
                    }
                },
                dialogTitle = {
                    Text(text = "Create account?")
                },
                dialogContent = {
                    Text(text = "We couldn't find an account with the email you provided. Would you like to create one?")
                },
                icon = {
                    Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                }
            )
        }

        // Email input
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(32),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            value = email.value,
            label = {
                Text(
                    text = "Email",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onValueChange = {
                viewModel.onEmailChange(it)
            },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Email, contentDescription = "")
            }
        )

        // Password Input
        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true,
            shape = RoundedCornerShape(32),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            value = password.value,
            label = {
                Text(
                    text = "Password",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onValueChange = {
                viewModel.onPasswordChange(it)
            },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Lock, contentDescription = "")
            }
        )

        // Login button
        val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current
        Button(modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(top = 12.dp),
            onClick = {
                localSoftwareKeyboardController?.hide()

                scope.launch {
                    viewModel.onSignIn(context).apply {
                        if (authStatus.value == "User not found") {
                            notFound.value = true
                        }
                    }
                }
            }) {

            Text("Log in / Sign up")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MemoTheme {
        LoginScreen(
            viewModel = LoginViewModelPreview(),
            // intentional
            context = LocalContext.current
        )
    }
}
