package dev.chara.tasks.shared.ui.picker

import androidx.compose.runtime.Composable
import com.github.michaelbull.result.Result

@Composable
expect fun photoPicker(onResult: (Result<ByteArray, Throwable>) -> Unit): () -> Unit