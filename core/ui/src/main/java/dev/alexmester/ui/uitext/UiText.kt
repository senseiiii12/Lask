package dev.alexmester.ui.uitext

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class StringResource(
        @param:StringRes val resId: Int,
        val args: Array<Any> = emptyArray(),
    ) : UiText {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StringResource

            if (resId != other.resId) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = resId
            result = 31 * result + args.contentHashCode()
            return result
        }
    }

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