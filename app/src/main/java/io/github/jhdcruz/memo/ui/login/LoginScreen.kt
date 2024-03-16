package io.github.jhdcruz.memo.ui.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import io.github.jhdcruz.memo.domain.auth.AuthViewModel
import io.github.jhdcruz.memo.domain.auth.AuthViewModelImpl
import io.github.jhdcruz.memo.domain.auth.AuthViewModelPreview
import io.github.jhdcruz.memo.ui.shared.GoogleButton
import io.github.jhdcruz.memo.ui.theme.MemoTheme
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
    viewModel: AuthViewModel = hiltViewModel<AuthViewModelImpl>(),
    context: Context,
) {
    val scope = rememberCoroutineScope()

    // Launch sign in flow on initial start
    LaunchedEffect(true) {
        scope.launch {
            viewModel.onSignIn(context)
        }
    }

    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxHeight()
                .padding(20.dp)
        ) {

            Spacer(modifier = Modifier.weight(1f))

            Text(
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                text = "Let's get you started!"
            )

            Spacer(modifier = Modifier.height(24.dp))

            LoginForm(
                modifier = modifier,
                context = context,
                viewModel = viewModel
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
                    text = "Or"
                )

                HorizontalDivider(
                    modifier = Modifier
                        .weight(1F)
                        .padding(vertical = 12.dp)
                )
            }

            // manual google sign-in if
            // credential manager is not available/preferred
            GoogleButton { viewModel.onGoogleSignIn(context) }

            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(vertical = 12.dp),
                onClick = { viewModel.onSignIn(context) }
            ) {
                Text("Login using Credential Manager")
            }

            Spacer(modifier = Modifier.weight(1f))

            ClickableText(
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally),
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
    modifier: Modifier,
    context: Context,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val email = viewModel.email.collectAsState(initial = "")
    val password = viewModel.password.collectAsState(initial = "")

    Column {
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
            .height(62.dp)
            .padding(top = 12.dp),
            onClick = {
                localSoftwareKeyboardController?.hide()
                viewModel.onSignUp(context)
            }) {

            Text("Log in / Sign up")
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    MemoTheme {
        LoginScreen(
            viewModel = AuthViewModelPreview(),
            // intentional
            context = LocalContext.current
        )
    }
}
