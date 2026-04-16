package dev.alexmester.impl.presentation.locale_picker.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.utils.BuildLocale
import dev.alexmester.utils.checkCompatibility
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
        }
        _state.update { it.copy(pendingCode = code, compatibilityWarning = warning) }
    }

    private fun onResolveCompatibility(adaptSelf: Boolean) {
        val warning = _state.value.compatibilityWarning ?: return
        when (type) {
            LocalePickerType.LANGUAGE -> {
                if (adaptSelf) {
                    _state.update {
                        it.copy(
                            pendingCode = warning.suggestedLanguage,
                            compatibilityWarning = null,
                        )
                    }
                    emitSideEffect(
                        LocalePickerSideEffect.ApplyWithLanguageOverride(warning.suggestedLanguage)
                    )
                    applySelection()
                } else {
                    _state.update { it.copy(compatibilityWarning = null) }
                    emitSideEffect(
                        LocalePickerSideEffect.ApplyWithCountryOverride(warning.suggestedCountry)
                    )
                    applySelection()
                }
            }
            LocalePickerType.COUNTRY -> {
                if (adaptSelf) {
                    _state.update {
                        it.copy(
                            pendingCode = warning.suggestedCountry,
                            compatibilityWarning = null,
                        )
                    }
                    emitSideEffect(
                        LocalePickerSideEffect.ApplyWithCountryOverride(warning.suggestedCountry)
                    )
                    applySelection()
                } else {
                    _state.update { it.copy(compatibilityWarning = null) }
                    emitSideEffect(
                        LocalePickerSideEffect.ApplyWithLanguageOverride(warning.suggestedLanguage)
                    )
                    applySelection()
                }
            }
        }
    }

    private fun loadItems() {
        viewModelScope.launch {
            val prefs = preferencesDataSource.userPreferences.first()
            val currentCode = when (type) {
                LocalePickerType.COUNTRY  -> prefs.defaultCountry
                LocalePickerType.LANGUAGE -> prefs.defaultLanguage
            }
            val otherCode = when (type) {
                LocalePickerType.COUNTRY  -> prefs.defaultLanguage
                LocalePickerType.LANGUAGE -> prefs.defaultCountry
            }
            val items = when (type) {
                LocalePickerType.COUNTRY  -> BuildLocale.buildCountryItems()
                LocalePickerType.LANGUAGE -> BuildLocale.buildLanguageItems()
            }
            _state.update {
                it.copy(
                    items = items,
                    selectedCode = currentCode,
                    pendingCode = currentCode,
                    otherLocaleCode = otherCode,
                )
            }
        }
    }

    private fun applySelection() {
        val pending = _state.value.pendingCode
        if (pending.isEmpty()) return
        viewModelScope.launch {
            saveLocale(pending)
            _state.update { it.copy(selectedCode = pending) }
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
        }
    }

    private fun emitSideEffect(effect: LocalePickerSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}