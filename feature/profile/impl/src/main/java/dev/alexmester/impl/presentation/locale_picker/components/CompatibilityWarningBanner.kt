package dev.alexmester.impl.presentation.locale_picker.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.utils.CompatibilityWarning
import java.util.Locale

@Composable
internal fun CompatibilityWarningBanner(
    modifier: Modifier = Modifier,
    type: LocalePickerType,
    warning: CompatibilityWarning?,
    selectedCode: String,
    currentCode: String,
    onAdaptSelf: () -> Unit,
    onAdaptOther: () -> Unit,
    onDismiss: () -> Unit,
) {

    var lastWarning by remember { mutableStateOf(warning) }
    if (warning != null) lastWarning = warning

    AnimatedVisibility(
        modifier = modifier,
        visible = warning != null,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        lastWarning?.let { w ->
            WarningBannerContent(
                type = type,
                warning = w,
                selectedCode = selectedCode,
                currentCode = currentCode,
                onAdaptSelf = onAdaptSelf,
                onAdaptOther = onAdaptOther,
                onDismiss = onDismiss,
            )
        }
    }
}

@Composable
private fun WarningBannerContent(
    type: LocalePickerType,
    warning: CompatibilityWarning,
    selectedCode: String,
    currentCode: String,
    onAdaptSelf: () -> Unit,
    onAdaptOther: () -> Unit,
    onDismiss: () -> Unit,
) {

    val selectedName: String
    val currentName: String
    val suggestedSelfName: String
    val suggestedOtherName: String

    when (type) {
        LocalePickerType.LANGUAGE -> {
            selectedName = Locale(selectedCode).getDisplayLanguage(Locale.ENGLISH)
                .replaceFirstChar { it.uppercase() }
            currentName = Locale("", currentCode.uppercase()).getDisplayCountry(Locale.ENGLISH)
                .replaceFirstChar { it.uppercase() }
            suggestedSelfName = Locale(warning.suggestedLanguage).getDisplayLanguage(Locale.ENGLISH)
                .replaceFirstChar { it.uppercase() }
            suggestedOtherName = Locale("", warning.suggestedCountry.uppercase())
                .getDisplayCountry(Locale.ENGLISH)
                .replaceFirstChar { it.uppercase() }
        }
        LocalePickerType.COUNTRY -> {
            // Пользователь выбирает страну → pending = новая страна, other = текущий язык
            selectedName = Locale("", selectedCode.uppercase()).getDisplayCountry(Locale.ENGLISH)
                .replaceFirstChar { it.uppercase() }
            currentName = Locale(currentCode).getDisplayLanguage(Locale.ENGLISH)
                .replaceFirstChar { it.uppercase() }
            suggestedSelfName = Locale("", warning.suggestedCountry.uppercase())
                .getDisplayCountry(Locale.ENGLISH)
                .replaceFirstChar { it.uppercase() }
            suggestedOtherName = Locale(warning.suggestedLanguage).getDisplayLanguage(Locale.ENGLISH)
                .replaceFirstChar { it.uppercase() }
        }
        LocalePickerType.AUTO_TRANSLATE_LANGUAGE ->{
            selectedName = ""
            currentName = ""
            suggestedSelfName = ""
            suggestedOtherName = ""
        }
    }

    val descriptionText = when (type) {
        LocalePickerType.LANGUAGE ->
            "Язык «$selectedName» не используется в стране «$currentName» — лента скорее всего будет пустой."
        LocalePickerType.COUNTRY ->
            "В стране «$selectedName» не используют язык «$currentName» — лента скорее всего будет пустой."

        LocalePickerType.AUTO_TRANSLATE_LANGUAGE -> ""
    }

    val adaptSelfLabel = when (type) {
        LocalePickerType.LANGUAGE -> "Язык → $suggestedSelfName"
        LocalePickerType.COUNTRY  -> "Страна → $suggestedSelfName"
        LocalePickerType.AUTO_TRANSLATE_LANGUAGE -> ""
    }

    val adaptOtherLabel = when (type) {
        LocalePickerType.LANGUAGE -> "Страна → $suggestedOtherName"
        LocalePickerType.COUNTRY  -> "Язык → $suggestedOtherName"
        LocalePickerType.AUTO_TRANSLATE_LANGUAGE -> ""
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = MaterialTheme.LaskColors.backgroundSecondary,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.LaskColors.warning,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Несовместимые язык и страна",
                    style = MaterialTheme.LaskTypography.body2SemiBold,
                    color = MaterialTheme.LaskColors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = MaterialTheme.LaskColors.textSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = descriptionText,
                style = MaterialTheme.LaskTypography.footnote,
                color = MaterialTheme.LaskColors.textSecondary,
            )
            Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
            ) {
                OutlinedButton(
                    onClick = onAdaptSelf,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = adaptSelfLabel,
                        style = MaterialTheme.LaskTypography.footnote,
                        color = MaterialTheme.LaskColors.textPrimary,
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }
                OutlinedButton(
                    onClick = onAdaptOther,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = adaptOtherLabel,
                        style = MaterialTheme.LaskTypography.footnote,
                        color = MaterialTheme.LaskColors.textPrimary,
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}