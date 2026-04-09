package dev.alexmester.impl.presentation.mvi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.database.dao.ReadingHistoryDao
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.impl.domain.interactor.ProfileInteractor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProfileViewModel(
    private val profileInteractor: ProfileInteractor,
//    private val preferencesDataSource: UserPreferencesDataSource,
//    private val readingHistoryDao: ReadingHistoryDao,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _sideEffects = Channel<ProfileSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        observeProfile()
        updateStreak()
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.OnInitEditMode -> initEditMode()

            is ProfileIntent.OnApplyEditChanges -> applyEditChanges()

            is ProfileIntent.OnCancelInitMode -> cancelEditMode()

            is ProfileIntent.OnProfileNameChange -> onProfileNameChange(intent.value)

            is ProfileIntent.OnProfileAvatarChange -> onProfileAvatarChange(intent.value)

            is ProfileIntent.NavigateToReadArticles ->
                emitSideEffect(ProfileSideEffect.NavigateToArticleList(ArticleListType.READ))

            is ProfileIntent.NavigateToClappedArticles ->
                emitSideEffect(ProfileSideEffect.NavigateToArticleList(ArticleListType.CLAPPED))
        }
    }

    private fun observeProfile() {
        profileInteractor.observeProfile()
            .onEach{ pair ->
                Log.d("observeProfile", pair.first.toString())
                _state.update { current ->
                    current.copy(
                        profileName = pair.first.profileName,
                        avatarUri = pair.first.avatarUri,
                        streakCount = pair.first.streakCount,
                        currentLevel = pair.first.currentLevel,
                        currentXp = pair.first.currentXp,
                        articleReadCount = pair.second,
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun updateStreak() {
        viewModelScope.launch {
            profileInteractor.updateStreak()
        }
    }

    private fun initEditMode(){
        _state.update { current ->
            current.copy(
                isEditingMode = true,
                editNameDraft = _state.value.profileName
            )
        }
    }

    private fun applyEditChanges() {
        viewModelScope.launch {
            profileInteractor.applyEditChanges(
                imageUri = _state.value.editAvatarUriDraft ?: _state.value.avatarUri,
                name = _state.value.editNameDraft
            )
        }.invokeOnCompletion {
            cancelEditMode()
        }
    }

    private fun cancelEditMode() {
        _state.update { current ->
            current.copy(
                editNameDraft = "",
                editAvatarUriDraft = null,
                isEditingMode = false
            )
        }
    }

    private fun onProfileNameChange(value: String){
        _state.update { current ->
            current.copy(editNameDraft = value)
        }
    }

    private fun onProfileAvatarChange(value: String?){
        _state.update { current ->
            current.copy(editAvatarUriDraft = value)
        }
    }


    private fun emitSideEffect(effect: ProfileSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}