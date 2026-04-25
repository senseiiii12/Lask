package dev.alexmester.ui.uitext

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {

    data class StringResource(
        @param:StringRes val resId: Int,
        val args: Array<Any> = emptyArray(),
    ) : UiText

    data class DynamicString(val value: String) : UiText

    @Composable
    fun asString(): String = when (this) {
        is StringResource -> stringResource(resId, *args)
        is DynamicString -> value
    }

    fun asString(context: Context): String = when (this) {
        is StringResource -> context.getString(resId, *args)
        is DynamicString -> value
    }
}