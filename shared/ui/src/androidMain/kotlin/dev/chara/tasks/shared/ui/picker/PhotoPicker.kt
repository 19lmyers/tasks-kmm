package dev.chara.tasks.shared.ui.picker

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun photoPicker(onResult: (Result<ByteArray, Throwable>) -> Unit): () -> Unit {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            selectedPhotoUri ->
            if (selectedPhotoUri != null) {
                coroutineScope
                    .launch {
                        val bitmap =
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source =
                                    ImageDecoder.createSource(
                                        context.contentResolver,
                                        selectedPhotoUri
                                    )
                                ImageDecoder.decodeBitmap(source)
                            } else {
                                @Suppress("DEPRECATION")
                                MediaStore.Images.Media.getBitmap(
                                    context.contentResolver,
                                    selectedPhotoUri
                                )
                            }

                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream)

                        withContext(Dispatchers.Main) { onResult(Ok(stream.toByteArray())) }
                    }
                    .invokeOnCompletion { error ->
                        if (error != null) {
                            onResult(Err(error))
                        }
                    }
            }
        }

    return {
        activityResultLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
}
