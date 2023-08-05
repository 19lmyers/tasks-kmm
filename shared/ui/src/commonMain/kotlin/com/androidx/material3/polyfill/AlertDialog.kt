/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androidx.material3.polyfill

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.androidx.material3.polyfill.internal.AlertDialogContent
import com.androidx.material3.polyfill.internal.AlertDialogFlowRow
import com.androidx.material3.polyfill.internal.ButtonsCrossAxisSpacing
import com.androidx.material3.polyfill.internal.ButtonsMainAxisSpacing
import com.androidx.material3.polyfill.internal.DialogMaxWidth
import com.androidx.material3.polyfill.internal.DialogMinWidth
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHigh
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHighest

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Box(
            modifier =
                modifier
                    .sizeIn(minWidth = DialogMinWidth, maxWidth = DialogMaxWidth)
                    .then(Modifier.semantics { paneTitle = "Dialog" }),
            propagateMinConstraints = true
        ) {
            content()
        }
    }
}

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    iconContentColor: Color = MaterialTheme.colorScheme.secondary,
    titleContentColor: Color = MaterialTheme.colorScheme.onSurface,
    textContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest),
    tonalElevation: Dp = 6.0.dp,
    properties: DialogProperties = DialogProperties()
) =
    AlertDialog(onDismissRequest = onDismissRequest, modifier = modifier, properties = properties) {
        AlertDialogContent(
            buttons = {
                AlertDialogFlowRow(
                    mainAxisSpacing = ButtonsMainAxisSpacing,
                    crossAxisSpacing = ButtonsCrossAxisSpacing
                ) {
                    dismissButton?.invoke()
                    confirmButton()
                }
            },
            icon = icon,
            title = title,
            text = text,
            shape = shape,
            containerColor = containerColor,
            tonalElevation = tonalElevation,
            buttonContentColor = MaterialTheme.colorScheme.primary,
            iconContentColor = iconContentColor,
            titleContentColor = titleContentColor,
            textContentColor = textContentColor,
            border = border
        )
    }
