/*
 * Copyright 2022 The Android Open Source Project
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

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@ExperimentalMaterial3Api
@Composable
fun ExposedDropdownMenuBox(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ExposedDropdownMenuBoxScope.() -> Unit
) {
    val density = LocalDensity.current

    var width by remember { mutableStateOf(0) }

    val focusRequester = remember { FocusRequester() }

    val scope = remember {
        object : ExposedDropdownMenuBoxScope {
            override fun Modifier.menuAnchor(): Modifier {
                return composed {
                        onGloballyPositioned { width = it.size.width }
                            .expandable(
                                expanded,
                                onExpandedChange = { onExpandedChange(!expanded) }
                            )
                    }
                    .focusRequester(focusRequester)
            }

            override fun Modifier.exposedDropdownSize(matchTextFieldWidth: Boolean): Modifier {
                return with(density) {
                    if (matchTextFieldWidth) {
                        width(width.toDp())
                    } else {
                        modifier
                    }
                }
            }
        }
    }

    Box(modifier) { scope.content() }

    SideEffect { if (expanded) focusRequester.requestFocus() }
}

/** Scope for [ExposedDropdownMenuBox]. */
@ExperimentalMaterial3Api
interface ExposedDropdownMenuBoxScope {
    /**
     * Modifier which should be applied to a [TextField] (or [OutlinedTextField]) placed inside the
     * scope. It's responsible for properly anchoring the [ExposedDropdownMenu], handling semantics
     * of the component, and requesting focus.
     */
    fun Modifier.menuAnchor(): Modifier

    /**
     * Modifier which should be applied to an [ExposedDropdownMenu] placed inside the scope. It's
     * responsible for setting the width of the [ExposedDropdownMenu], which will match the width of
     * the [TextField] (if [matchTextFieldWidth] is set to true). It will also change the height of
     * [ExposedDropdownMenu], so it'll take the largest possible height to not overlap the
     * [TextField] and the software keyboard.
     *
     * @param matchTextFieldWidth whether the menu should match the width of the text field to which
     *   it's attached. If set to `true`, the width will match the width of the text field.
     */
    fun Modifier.exposedDropdownSize(matchTextFieldWidth: Boolean = true): Modifier

    /**
     * Popup which contains content for Exposed Dropdown Menu. Should be used inside the content of
     * [ExposedDropdownMenuBox].
     *
     * @param expanded whether the menu is expanded
     * @param onDismissRequest called when the user requests to dismiss the menu, such as by tapping
     *   outside the menu's bounds
     * @param modifier the [Modifier] to be applied to this menu
     * @param content the content of the menu
     */
    @Composable
    fun ExposedDropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable ColumnScope.() -> Unit
    ) {
        return DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = modifier.exposedDropdownSize().zIndex(1f),
            content = content
        )
    }
}

@ExperimentalMaterial3Api
object ExposedDropdownMenuDefaults {
    /**
     * Default trailing icon for Exposed Dropdown Menu.
     *
     * @param expanded whether [ExposedDropdownMenuBoxScope.ExposedDropdownMenu] is expanded or not.
     *   Affects the appearance of the icon.
     */
    @ExperimentalMaterial3Api
    @Composable
    fun TrailingIcon(expanded: Boolean) {
        Icon(Icons.Filled.ArrowDropDown, null, Modifier.rotate(if (expanded) 180f else 0f))
    }

    /**
     * Creates a [TextFieldColors] that represents the default input text, container, and content
     * colors (including label, placeholder, icons, etc.) used in a [TextField] within an
     * [ExposedDropdownMenuBox].
     *
     * @param focusedTextColor the color used for the input text of this text field when focused
     * @param unfocusedTextColor the color used for the input text of this text field when not
     *   focused
     * @param disabledTextColor the color used for the input text of this text field when disabled
     * @param errorTextColor the color used for the input text of this text field when in error
     *   state
     * @param focusedContainerColor the container color for this text field when focused
     * @param unfocusedContainerColor the container color for this text field when not focused
     * @param disabledContainerColor the container color for this text field when disabled
     * @param errorContainerColor the container color for this text field when in error state
     * @param cursorColor the cursor color for this text field
     * @param errorCursorColor the cursor color for this text field when in error state
     * @param selectionColors the colors used when the input text of this text field is selected
     * @param focusedIndicatorColor the indicator color for this text field when focused
     * @param unfocusedIndicatorColor the indicator color for this text field when not focused
     * @param disabledIndicatorColor the indicator color for this text field when disabled
     * @param errorIndicatorColor the indicator color for this text field when in error state
     * @param focusedLeadingIconColor the leading icon color for this text field when focused
     * @param unfocusedLeadingIconColor the leading icon color for this text field when not focused
     * @param disabledLeadingIconColor the leading icon color for this text field when disabled
     * @param errorLeadingIconColor the leading icon color for this text field when in error state
     * @param focusedTrailingIconColor the trailing icon color for this text field when focused
     * @param unfocusedTrailingIconColor the trailing icon color for this text field when not
     *   focused
     * @param disabledTrailingIconColor the trailing icon color for this text field when disabled
     * @param errorTrailingIconColor the trailing icon color for this text field when in error state
     * @param focusedLabelColor the label color for this text field when focused
     * @param unfocusedLabelColor the label color for this text field when not focused
     * @param disabledLabelColor the label color for this text field when disabled
     * @param errorLabelColor the label color for this text field when in error state
     * @param focusedPlaceholderColor the placeholder color for this text field when focused
     * @param unfocusedPlaceholderColor the placeholder color for this text field when not focused
     * @param disabledPlaceholderColor the placeholder color for this text field when disabled
     * @param errorPlaceholderColor the placeholder color for this text field when in error state
     * @param focusedPrefixColor the prefix color for this text field when focused
     * @param unfocusedPrefixColor the prefix color for this text field when not focused
     * @param disabledPrefixColor the prefix color for this text field when disabled
     * @param errorPrefixColor the prefix color for this text field when in error state
     * @param focusedSuffixColor the suffix color for this text field when focused
     * @param unfocusedSuffixColor the suffix color for this text field when not focused
     * @param disabledSuffixColor the suffix color for this text field when disabled
     * @param errorSuffixColor the suffix color for this text field when in error state
     */
    @Composable
    fun textFieldColors(
        focusedTextColor: Color = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor: Color = MaterialTheme.colorScheme.onSurface,
        disabledTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorTextColor: Color = MaterialTheme.colorScheme.onSurface,
        focusedContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        errorContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        cursorColor: Color = MaterialTheme.colorScheme.primary,
        errorCursorColor: Color = MaterialTheme.colorScheme.error,
        selectionColors: TextSelectionColors = LocalTextSelectionColors.current,
        focusedIndicatorColor: Color = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledIndicatorColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorIndicatorColor: Color = MaterialTheme.colorScheme.error,
        focusedLeadingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedLeadingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLeadingIconColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorLeadingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedTrailingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedTrailingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorTrailingIconColor: Color = MaterialTheme.colorScheme.error,
        focusedLabelColor: Color = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor: Color = MaterialTheme.colorScheme.onSurface,
        errorLabelColor: Color = MaterialTheme.colorScheme.error,
        focusedPlaceholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledPlaceholderColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorPlaceholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedPrefixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPrefixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledPrefixColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorPrefixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedSuffixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedSuffixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledSuffixColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorSuffixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ): TextFieldColors =
        TextFieldDefaults.colors(
            focusedTextColor = focusedTextColor,
            unfocusedTextColor = unfocusedTextColor,
            disabledTextColor = disabledTextColor,
            errorTextColor = errorTextColor,
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = unfocusedContainerColor,
            disabledContainerColor = disabledContainerColor,
            errorContainerColor = errorContainerColor,
            cursorColor = cursorColor,
            errorCursorColor = errorCursorColor,
            selectionColors = selectionColors,
            focusedIndicatorColor = focusedIndicatorColor,
            unfocusedIndicatorColor = unfocusedIndicatorColor,
            disabledIndicatorColor = disabledIndicatorColor,
            errorIndicatorColor = errorIndicatorColor,
            focusedLeadingIconColor = focusedLeadingIconColor,
            unfocusedLeadingIconColor = unfocusedLeadingIconColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            errorLeadingIconColor = errorLeadingIconColor,
            focusedTrailingIconColor = focusedTrailingIconColor,
            unfocusedTrailingIconColor = unfocusedTrailingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
            errorTrailingIconColor = errorTrailingIconColor,
            focusedLabelColor = focusedLabelColor,
            unfocusedLabelColor = unfocusedLabelColor,
            disabledLabelColor = disabledLabelColor,
            errorLabelColor = errorLabelColor,
            focusedPlaceholderColor = focusedPlaceholderColor,
            unfocusedPlaceholderColor = unfocusedPlaceholderColor,
            disabledPlaceholderColor = disabledPlaceholderColor,
            errorPlaceholderColor = errorPlaceholderColor,
            focusedPrefixColor = focusedPrefixColor,
            unfocusedPrefixColor = unfocusedPrefixColor,
            disabledPrefixColor = disabledPrefixColor,
            errorPrefixColor = errorPrefixColor,
            focusedSuffixColor = focusedSuffixColor,
            unfocusedSuffixColor = unfocusedSuffixColor,
            disabledSuffixColor = disabledSuffixColor,
            errorSuffixColor = errorSuffixColor,
        )

    /**
     * Creates a [TextFieldColors] that represents the default input text, container, and content
     * colors (including label, placeholder, icons, etc.) used in an [OutlinedTextField] within an
     * [ExposedDropdownMenuBox].
     *
     * @param focusedTextColor the color used for the input text of this text field when focused
     * @param unfocusedTextColor the color used for the input text of this text field when not
     *   focused
     * @param disabledTextColor the color used for the input text of this text field when disabled
     * @param errorTextColor the color used for the input text of this text field when in error
     *   state
     * @param focusedContainerColor the container color for this text field when focused
     * @param unfocusedContainerColor the container color for this text field when not focused
     * @param disabledContainerColor the container color for this text field when disabled
     * @param errorContainerColor the container color for this text field when in error state
     * @param cursorColor the cursor color for this text field
     * @param errorCursorColor the cursor color for this text field when in error state
     * @param selectionColors the colors used when the input text of this text field is selected
     * @param focusedBorderColor the border color for this text field when focused
     * @param unfocusedBorderColor the border color for this text field when not focused
     * @param disabledBorderColor the border color for this text field when disabled
     * @param errorBorderColor the border color for this text field when in error state
     * @param focusedLeadingIconColor the leading icon color for this text field when focused
     * @param unfocusedLeadingIconColor the leading icon color for this text field when not focused
     * @param disabledLeadingIconColor the leading icon color for this text field when disabled
     * @param errorLeadingIconColor the leading icon color for this text field when in error state
     * @param focusedTrailingIconColor the trailing icon color for this text field when focused
     * @param unfocusedTrailingIconColor the trailing icon color for this text field when not
     *   focused
     * @param disabledTrailingIconColor the trailing icon color for this text field when disabled
     * @param errorTrailingIconColor the trailing icon color for this text field when in error state
     * @param focusedLabelColor the label color for this text field when focused
     * @param unfocusedLabelColor the label color for this text field when not focused
     * @param disabledLabelColor the label color for this text field when disabled
     * @param errorLabelColor the label color for this text field when in error state
     * @param focusedPlaceholderColor the placeholder color for this text field when focused
     * @param unfocusedPlaceholderColor the placeholder color for this text field when not focused
     * @param disabledPlaceholderColor the placeholder color for this text field when disabled
     * @param errorPlaceholderColor the placeholder color for this text field when in error state
     * @param focusedPrefixColor the prefix color for this text field when focused
     * @param unfocusedPrefixColor the prefix color for this text field when not focused
     * @param disabledPrefixColor the prefix color for this text field when disabled
     * @param errorPrefixColor the prefix color for this text field when in error state
     * @param focusedSuffixColor the suffix color for this text field when focused
     * @param unfocusedSuffixColor the suffix color for this text field when not focused
     * @param disabledSuffixColor the suffix color for this text field when disabled
     * @param errorSuffixColor the suffix color for this text field when in error state
     */
    @Composable
    fun outlinedTextFieldColors(
        focusedTextColor: Color = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorTextColor: Color = MaterialTheme.colorScheme.onSurface,
        focusedContainerColor: Color = Color.Transparent,
        unfocusedContainerColor: Color = Color.Transparent,
        disabledContainerColor: Color = Color.Transparent,
        errorContainerColor: Color = Color.Transparent,
        cursorColor: Color = MaterialTheme.colorScheme.primary,
        errorCursorColor: Color = MaterialTheme.colorScheme.error,
        selectionColors: TextSelectionColors = LocalTextSelectionColors.current,
        focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor: Color = MaterialTheme.colorScheme.outline,
        disabledBorderColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        errorBorderColor: Color = MaterialTheme.colorScheme.error,
        focusedLeadingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedLeadingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLeadingIconColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorLeadingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedTrailingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedTrailingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorTrailingIconColor: Color = MaterialTheme.colorScheme.error,
        focusedLabelColor: Color = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorLabelColor: Color = MaterialTheme.colorScheme.error,
        focusedPlaceholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledPlaceholderColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorPlaceholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedPrefixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPrefixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledPrefixColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorPrefixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedSuffixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedSuffixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledSuffixColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorSuffixColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ): TextFieldColors =
        OutlinedTextFieldDefaults.colors(
            focusedTextColor = focusedTextColor,
            unfocusedTextColor = unfocusedTextColor,
            disabledTextColor = disabledTextColor,
            errorTextColor = errorTextColor,
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = unfocusedContainerColor,
            disabledContainerColor = disabledContainerColor,
            errorContainerColor = errorContainerColor,
            cursorColor = cursorColor,
            errorCursorColor = errorCursorColor,
            selectionColors = selectionColors,
            focusedBorderColor = focusedBorderColor,
            unfocusedBorderColor = unfocusedBorderColor,
            disabledBorderColor = disabledBorderColor,
            errorBorderColor = errorBorderColor,
            focusedLeadingIconColor = focusedLeadingIconColor,
            unfocusedLeadingIconColor = unfocusedLeadingIconColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            errorLeadingIconColor = errorLeadingIconColor,
            focusedTrailingIconColor = focusedTrailingIconColor,
            unfocusedTrailingIconColor = unfocusedTrailingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
            errorTrailingIconColor = errorTrailingIconColor,
            focusedLabelColor = focusedLabelColor,
            unfocusedLabelColor = unfocusedLabelColor,
            disabledLabelColor = disabledLabelColor,
            errorLabelColor = errorLabelColor,
            focusedPlaceholderColor = focusedPlaceholderColor,
            unfocusedPlaceholderColor = unfocusedPlaceholderColor,
            disabledPlaceholderColor = disabledPlaceholderColor,
            errorPlaceholderColor = errorPlaceholderColor,
            focusedPrefixColor = focusedPrefixColor,
            unfocusedPrefixColor = unfocusedPrefixColor,
            disabledPrefixColor = disabledPrefixColor,
            errorPrefixColor = errorPrefixColor,
            focusedSuffixColor = focusedSuffixColor,
            unfocusedSuffixColor = unfocusedSuffixColor,
            disabledSuffixColor = disabledSuffixColor,
            errorSuffixColor = errorSuffixColor,
        )

    /**
     * Padding for [DropdownMenuItem]s within [ExposedDropdownMenuBoxScope.ExposedDropdownMenu] to
     * align them properly with [TextField] components.
     */
    val ItemContentPadding: PaddingValues =
        PaddingValues(horizontal = ExposedDropdownMenuItemHorizontalPadding, vertical = 0.dp)
}

@Suppress("ComposableModifierFactory")
@Composable
private fun Modifier.expandable(expanded: Boolean, onExpandedChange: () -> Unit) =
    pointerInput(Unit) {
            awaitEachGesture {
                // Must be PointerEventPass.Initial to observe events before the text field consumes
                // them
                // in the Main pass
                awaitFirstDown(pass = PointerEventPass.Initial)
                val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                if (upEvent != null) {
                    onExpandedChange()
                }
            }
        }
        .semantics {
            stateDescription = if (expanded) "Expanded" else "Collapsed"
            contentDescription = "Dropdown menu"
            onClick {
                onExpandedChange()
                true
            }
        }

private val ExposedDropdownMenuItemHorizontalPadding = 16.dp

internal val MenuVerticalMargin = 48.dp
private val DropdownMenuItemHorizontalPadding = 12.dp
internal val DropdownMenuVerticalPadding = 8.dp
private val DropdownMenuItemDefaultMinWidth = 112.dp
private val DropdownMenuItemDefaultMaxWidth = 280.dp
