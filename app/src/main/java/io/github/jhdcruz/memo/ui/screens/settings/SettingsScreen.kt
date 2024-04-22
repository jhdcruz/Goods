package io.github.jhdcruz.memo.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.data.model.User
import io.github.jhdcruz.memo.ui.navigation.BottomNavigation
import io.github.jhdcruz.memo.ui.theme.MemoTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel<SettingsViewModelImpl>(),
    photoUrl: String? = null,
) {
    val user by settingsViewModel.user.collectAsState(User())

    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // use already loaded photoUrl if available
            ProfileItem(user = user, photoUrl = photoUrl)

            HorizontalDivider(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                color = Color.LightGray,
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "Settings",
            )

            ListItem(
                headlineContent = { Text(text = "Tags") },
                leadingContent = {
                    Image(
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        painter = painterResource(id = R.drawable.baseline_label_24),
                        contentDescription = null,
                    )
                },
                trailingContent = {
                    Icon(
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "Edit tags",
                    )
                },
            )

            ListItem(
                headlineContent = { Text(text = "Categories") },
                leadingContent = {
                    Image(
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        painter = painterResource(id = R.drawable.baseline_folder_24),
                        contentDescription = null,
                    )
                },
                trailingContent = {
                    Icon(
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "Edit categories",
                    )
                },
            )
        }
    }
}

@Composable
fun ProfileItem(
    user: User,
    modifier: Modifier = Modifier,
    photoUrl: String? = null,
) {
    ListItem(
        modifier =
            modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        headlineContent = {
            Text(
                text = user.name ?: "Set name",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        supportingContent = {
            Text(
                text = user.email ?: "Set email",
                maxLines = 1,
            )
        },
        leadingContent = {
            // user avatar
            AsyncImage(
                modifier =
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                model = photoUrl ?: user.photoUrl ?: "",
                contentDescription = "User avatar",
                placeholder = painterResource(id = R.drawable.baseline_user_circle_24),
                error = painterResource(id = R.drawable.baseline_user_circle_24),
            )
        },
        trailingContent = {
            Icon(
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "Edit Profile",
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    MemoTheme {
        val navController = rememberNavController()
        val settingsViewModel = SettingsViewModelPreview()

        Scaffold(
            bottomBar = {
                BottomNavigation(navController)
            },
        ) { innerPadding ->
            SettingsScreen(
                modifier = Modifier.padding(innerPadding),
                settingsViewModel = settingsViewModel,
            )
        }
    }
}
