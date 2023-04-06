package dev.chara.tasks.android.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.chara.tasks.android.ui.util.forwardingPainter
import dev.chara.tasks.data.getGravatarUrl
import dev.chara.tasks.model.Profile
import org.koin.compose.koinInject

@Composable
fun ProfileCard(
    profile: Profile,
    actions: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            ProfileImage(
                email = profile.email,
                profilePhotoUri = profile.profilePhotoUri,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .requiredSize(96.dp)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            actions()
        }
    }
}

@Preview
@Composable
fun Preview_ProfileCard() {
    ProfileCard(
        profile = Profile(
            email = "user@email.com",
            displayName = "User",
            profilePhotoUri = null
        )
    ) {
    }
}

@Composable
fun ProfileImage(
    email: String,
    profilePhotoUri: String?,
    modifier: Modifier = Modifier
) {
    val imageLoader = koinInject<ImageLoader>()

    // Make it work in preview by hardcoding the image URL
    val imageUrl = if (LocalInspectionMode.current) {
        null
    } else profilePhotoUri ?: getGravatarUrl(email)

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        imageLoader = imageLoader,
        contentDescription = "Profile picture",
        placeholder = forwardingPainter(
            painter = rememberVectorPainter(Icons.Filled.AccountCircle),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        ),
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(CircleShape)
    )
}

@Preview
@Composable
private fun Preview_ProfileImage() {
    ProfileImage(email = "user@email.com", profilePhotoUri = null)
}