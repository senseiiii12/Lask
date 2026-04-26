package dev.alexmester.impl.presentation.system.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.datastore.UserPreferencesDataSource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SystemViewModel(
    private val preferencesDataSource: UserPreferencesDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(SystemState())
    val state: StateFlow<SystemState> = _state.asStateFlow()

    private val _sideEffects = Channel<SystemSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        observePreferences()
    }

    fun handleIntent(intent: SystemIntent) {
        when (intent) {
            is SystemIntent.SetTheme -> saveTheme(intent.theme)
            is SystemIntent.NavigateToLanguage ->
                emitSideEffect(SystemSideEffect.NavigateToLocalePicker(LocalePickerType.LANGUAGE))
            is SystemIntent.NavigateToCountry ->
                emitSideEffect(SystemSideEffect.NavigateToLocalePicker(LocalePickerType.COUNTRY))
            is SystemIntent.NavigateToAutoTranslateLanguage ->
                emitSideEffect(SystemSideEffect.NavigateToAutoTranslatePicker)
            is SystemIntent.Back ->
                emitSideEffect(SystemSideEffect.NavigateBack)
        }
    }

    private fun observePreferences() {
        preferencesDataSource.userPreferences
            .onEach { prefs ->
                _state.update {
                    it.copy(
                        theme = when (prefs.isDarkTheme) {
                            true  -> AppTheme.DARK
                            false -> AppTheme.LIGHT
                            null  -> AppTheme.SYSTEM
                        },
                        languageCode = prefs.defaultLanguage,
                        countryCode = prefs.defaultCountry,
                        autoTranslateLanguage = prefs.autoTranslateLanguage,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun saveTheme(theme: AppTheme) {
        viewModelScope.launch {
            val isDark: Boolean? = when (theme) {
                AppTheme.SYSTEM -> null
                AppTheme.LIGHT  -> false
                AppTheme.DARK   -> true
            }
            preferencesDataSource.updateTheme(isDark)
        }
    }

    private fun emitSideEffect(effect: SystemSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}