package dev.chara.tasks.android.ui.route.profile

import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chara.tasks.viewmodel.profile.ProfileViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@Composable
fun ProfileRoute(
    navigateUp: () -> Unit,
    navigateToChangeEmail: () -> Unit,
    navigateToChangePassword: () -> Unit
) {
    val viewModel: ProfileViewModel = viewModel()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val selectProfilePhoto = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream)
                viewModel.updateUserProfilePhoto(stream.toByteArray())
            }
        }
    }

    if (!state.value.isLoading) {
        ProfileScreen(
            state = state.value,
            snackbarHostState = snackbarHostState,
            navigateUp = navigateUp,
            onChangePhotoClicked = {
                selectProfilePhoto.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            onChangeEmailClicked = navigateToChangeEmail,
            onChangePasswordClicked = navigateToChangePassword,
            onUpdateProfile = { profile ->
                viewModel.updateUserProfile(profile)
            }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    LaunchedEffect(viewModel.messages) {
        viewModel.messages.collect { message ->
            snackbarHostState.showSnackbar(
                message = message.text,
                duration = SnackbarDuration.Short,
                withDismissAction = true,
            )
        }
    }
}