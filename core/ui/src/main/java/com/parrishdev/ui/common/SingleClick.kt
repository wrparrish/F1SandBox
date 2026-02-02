package com.parrishdev.ui.common

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Debounce interval for click events to prevent double-clicks from
 * triggering multiple navigations or actions.
 */
private const val CLICK_DEBOUNCE_MS = 300L

/**
 * Remembers the last click time to enable debouncing across recompositions.
 */
class SingleClickHandler {
    private var lastClickTime: Long = 0

    fun shouldHandle(): Boolean {
        val now = System.currentTimeMillis()
        return if (now - lastClickTime >= CLICK_DEBOUNCE_MS) {
            lastClickTime = now
            true
        } else {
            false
        }
    }
}

/**
 * Returns a remembered [SingleClickHandler] that persists across recompositions.
 */
@Composable
fun rememberSingleClickHandler(): SingleClickHandler {
    return remember { SingleClickHandler() }
}

/**
 * A clickable modifier that debounces rapid clicks to prevent duplicate actions.
 *
 * Use this instead of [Modifier.clickable] when the click action should only
 * trigger once even if the user taps rapidly (e.g., navigation, form submission).
 *
 * @param enabled Whether the click is enabled
 * @param onClick The action to perform on click (debounced)
 */
@Composable
fun Modifier.singleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    val handler = rememberSingleClickHandler()
    val interactionSource = remember { MutableInteractionSource() }

    return this.clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        indication = LocalIndication.current,
        onClick = {
            if (handler.shouldHandle()) {
                onClick()
            }
        }
    )
}
