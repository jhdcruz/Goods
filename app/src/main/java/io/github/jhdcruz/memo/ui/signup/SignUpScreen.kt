package io.github.jhdcruz.memo.ui.signup

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.jhdcruz.memo.domain.signup.SignUpViewModel
import io.github.jhdcruz.memo.domain.signup.SignUpViewModelPreview
import io.github.jhdcruz.memo.ui.shared.GoogleButton

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    context: Context,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    Surface { ->
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxHeight()
                .padding(20.dp)
        ) {
            val email = viewModel.email.collectAsState(initial = "")
            val password = viewModel.password.collectAsState(initial = "")

            Text(
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                text = "Let's get you set up!"
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                label = {
                    Text(
                        text = "Email",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                maxLines = 1,
                shape = RoundedCornerShape(32),
                modifier = modifier.fillMaxWidth(),
                value = email.value,
                onValueChange = {
                    viewModel.onEmailChange(it)
                },
            )
            OutlinedTextField(
                label = {
                    Text(
                        text = "Password",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                maxLines = 1,
                shape = RoundedCornerShape(32),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                value = password.value,
                onValueChange = {
                    viewModel.onPasswordChange(it)
                },
            )

            val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current

            Button(modifier = modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
                onClick = {
                    localSoftwareKeyboardController?.hide()
                    viewModel.onSignUp(context)
                }) {

                Text("Sign up")
            }

            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1F)
                        .padding(vertical = 12.dp)
                )

                Text(modifier = Modifier.padding(horizontal = 6.dp), text = "Or")

                HorizontalDivider(
                    modifier = Modifier
                        .weight(1F)
                        .padding(vertical = 12.dp)
                )
            }

            GoogleButton {

            }
        }
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(
        navController = rememberNavController(),
        context = LocalContext.current,
        viewModel = SignUpViewModelPreview()
    )
}
