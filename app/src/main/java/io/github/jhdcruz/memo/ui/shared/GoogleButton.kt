package io.github.jhdcruz.memo.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jhdcruz.memo.R

@Composable
fun GoogleButton(
    modifier: Modifier = Modifier,
    action: () -> Unit
) {
    ElevatedButton(
        modifier = modifier
            .fillMaxWidth(),
        onClick = action
    ) {
        Image(
            painter = painterResource(id = R.drawable.i8_google_48),
            contentDescription = "Google Icon",
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Sign in with Google")
    }
}

@Preview
@Composable
fun GoogleButtonPreview() {
    GoogleButton {}
}
