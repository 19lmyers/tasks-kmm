package dev.chara.tasks.android.ui.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.chara.tasks.android.ui.util.forwardingPainter
import dev.chara.tasks.data.getGravatarUrl
import org.koin.compose.koinInject

@Composable
fun ProfileImage(
    email: String,
    profilePhotoUri: String?,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current) {
        Icon(Icons.Filled.AccountCircle, contentDescription = "Account options")
    } else {
        val imageLoader = koinInject<ImageLoader>()
        val imageUrl = profilePhotoUri ?: getGravatarUrl(email)

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
}