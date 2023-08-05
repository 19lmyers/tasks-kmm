package dev.chara.tasks.shared.ui.item

import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import co.touchlab.kermit.Logger
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun ProfileImage(
    email: String?,
    profilePhotoUri: String?,
    getGravatarUri: (String) -> String,
    modifier: Modifier = Modifier
) {
    if (email == null) {
        Icon(
            Icons.Filled.AccountCircle,
            contentDescription = "Account options",
            modifier = modifier
        )
    } else {
        KamelImage(
            resource = asyncPainterResource(profilePhotoUri ?: getGravatarUri(email)),
            contentDescription = "Account options",
            onLoading = {
                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = "Account options",
                    modifier = modifier
                )
            },
            onFailure = {
                Logger.e("Failed to load profile picture: ", it)
                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = "Account options",
                    modifier = modifier
                )
            },
            animationSpec = tween(),
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(CircleShape)
        )
    }
}
