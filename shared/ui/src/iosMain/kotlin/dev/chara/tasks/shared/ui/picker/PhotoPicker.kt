package dev.chara.tasks.shared.ui.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.LocalUIViewController
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.rickclephas.kmp.nserrorkt.asThrowable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Photos.PHPhotoLibrary
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UniformTypeIdentifiers.UTTypeJPEG
import platform.UniformTypeIdentifiers.loadDataRepresentationForContentType
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_sync
import platform.posix.memcpy

@Composable
actual fun photoPicker(onResult: (Result<ByteArray, Throwable>) -> Unit): () -> Unit {
    val uiViewController = LocalUIViewController.current

    val pickerDelegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                val result = didFinishPicking.firstOrNull() as? PHPickerResult

                if (result != null) {
                    result.itemProvider().loadDataRepresentationForContentType(UTTypeJPEG) {
                        data,
                        error ->
                        if (data != null) {
                            val bytes = data.toByteArray()

                            dispatch_sync(dispatch_get_main_queue()) {
                                onResult(Ok(bytes))

                                picker.dismissModalViewControllerAnimated(true)
                            }
                        } else if (error != null) {
                            dispatch_sync(dispatch_get_main_queue()) {
                                onResult(Err(error.asThrowable()))

                                picker.dismissModalViewControllerAnimated(true)
                            }
                        }
                    }
                } else {
                    picker.dismissModalViewControllerAnimated(true)
                }
            }
        }
    }

    val configuration = PHPickerConfiguration(PHPhotoLibrary.sharedPhotoLibrary())

    val filter = PHPickerFilter.anyFilterMatchingSubfilters(listOf(PHPickerFilter.imagesFilter))
    configuration.filter = filter

    return remember {
        {
            val pickerController = PHPickerViewController(configuration)
            pickerController.setDelegate(pickerDelegate)
            uiViewController.presentViewController(
                pickerController,
                animated = true,
                completion = null
            )
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray =
    ByteArray(length.toInt()).apply { usePinned { memcpy(it.addressOf(0), bytes, length) } }
