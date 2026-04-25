package dev.alexmester.impl.presentation.locale_picker.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.utils.locale.LocaleUtils
import dev.alexmester.utils.locale.checkCompatibility
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocalePickerViewModel(
    private val type: LocalePickerType,
    private val preferencesDataSource: UserPreferencesDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(LocalePickerState(type = type))
    val state: StateFlow<LocalePickerState> = _state.asStateFlow()

    private val _sideEffects = Channel<LocalePickerSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        loadItems()
    }

    fun handleIntent(intent: LocalePickerIntent) {
        when (intent) {
            is LocalePickerIntent.SelectItem -> onSelectItem(intent.code)
            is LocalePickerIntent.Apply -> applySelection()
            is LocalePickerIntent.Back -> emitSideEffect(LocalePickerSideEffect.NavigateBack)
            is LocalePickerIntent.ResolveCompatibility -> onResolveCompatibility(intent.adaptSelf)
            is LocalePickerIntent.DismissCompatibilityWarning ->
                _state.update { it.copy(compatibilityWarning = null) }
        }
    }

    private fun onSelectItem(code: String) {
        if (type == LocalePickerType.AUTO_TRANSLATE_LANGUAGE) {
            _state.update { it.copy(selectedCode = code, compatibilityWarning = null) }
            return
        }
        val current = _state.value
        val warning = when (type) {
            LocalePickerType.LANGUAGE -> checkCompatibility(
                language = code,
                country = current.otherLocaleCode,
            )
            LocalePickerType.COUNTRY -> checkCompatibility(
                language = current.otherLocaleCode,
                country = code,
            )
            LocalePickerType.AUTO_TRANSLATE_LANGUAGE -> null
        }
        _state.update { it.copy(selectedCode = code, compatibilityWarning = warning) }
    }

    private fun onResolveCompatibility(adaptSelf: Boolean) {
        val warning = _state.value.compatibilityWarning ?: return
        val pending = _state.value.selectedCode
        viewModelScope.launch {
            when (type) {
                LocalePickerType.LANGUAGE -> {
                    if (adaptSelf) {
                        _state.update {
                            it.copy(
                                selectedCode = warning.suggestedLanguage,
                                compatibilityWarning = null,
                            )
                        }
                    } else {
                        preferencesDataSource.updateLocaleManually(
                            country = warning.suggestedCountry,
                            language = pending,
                        )
                        _state.update {
                            it.copy(
                                currentCode = pending,
                                compatibilityWarning = null,
                            )
                        }
                        emitSideEffect(LocalePickerSideEffect.NavigateBack)
                    }
                }

                LocalePickerType.COUNTRY -> {
                    if (adaptSelf) {
                        _state.update {
                            it.copy(
                                selectedCode = warning.suggestedCountry,
                                compatibilityWarning = null,
                            )
                        }
                    } else {
                        preferencesDataSource.updateLocaleManually(
                            country = pending,
                            language = warning.suggestedLanguage,
                        )
                        _state.update {
                            it.copy(
                                currentCode = pending,
                                compatibilityWarning = null,
                            )
                        }
                        emitSideEffect(LocalePickerSideEffect.NavigateBack)
                    }
                }

                LocalePickerType.AUTO_TRANSLATE_LANGUAGE -> {
                    _state.update { it.copy(compatibilityWarning = null) }
                }
            }
        }
    }

    private fun loadItems() {
        viewModelScope.launch {
            val prefs = preferencesDataSource.userPreferences.first()
            when (type) {
                LocalePickerType.COUNTRY -> {
                    val currentCode = prefs.defaultCountry
                    val otherCode = prefs.defaultLanguage
                    _state.update {
                        it.copy(
                            items = LocaleUtils.buildCountryItems(),
                            currentCode = currentCode,
                            selectedCode = currentCode,
                            otherLocaleCode = otherCode,
                        )
                    }
                }

                LocalePickerType.LANGUAGE -> {
                    val currentCode = prefs.defaultLanguage
                    val otherCode = prefs.defaultCountry
                    _state.update {
                        it.copy(
                            items = LocaleUtils.buildLanguageItems(),
                            currentCode = currentCode,
                            selectedCode = currentCode,
                            otherLocaleCode = otherCode,
                        )
                    }
                }

                LocalePickerType.AUTO_TRANSLATE_LANGUAGE -> {
                    _state.update {
                        it.copy(
                            items = LocaleUtils.buildLanguageItems(),
                            currentCode = prefs.autoTranslateLanguage,
                            selectedCode = prefs.autoTranslateLanguage,
                            otherLocaleCode = "",
                        )
                    }
                }
            }
        }
    }

    private fun applySelection() {
        val pending = _state.value.selectedCode
        if (pending.isEmpty()) return
        viewModelScope.launch {
            saveLocale(pending)
            _state.update { it.copy(currentCode = pending) }
            emitSideEffect(LocalePickerSideEffect.NavigateBack)
        }
    }

    private suspend fun saveLocale(code: String) {
        val prefs = preferencesDataSource.userPreferences.first()
        when (type) {
            LocalePickerType.COUNTRY -> preferencesDataSource.updateLocaleManually(
                country = code,
                language = prefs.defaultLanguage,
            )
            LocalePickerType.LANGUAGE -> preferencesDataSource.updateLocaleManually(
                country = prefs.defaultCountry,
                language = code,
            )
            LocalePickerType.AUTO_TRANSLATE_LANGUAGE ->
                preferencesDataSource.updateAutoTranslateLanguage(code)
        }
    }

    private fun emitSideEffect(effect: LocalePickerSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}