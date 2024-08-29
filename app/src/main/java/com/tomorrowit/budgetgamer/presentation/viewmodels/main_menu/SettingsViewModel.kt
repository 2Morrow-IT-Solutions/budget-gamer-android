package com.tomorrowit.budgetgamer.presentation.viewmodels.main_menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.MessagingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val messagingRepo: MessagingRepo
) : ViewModel() {

    private val _state = MutableStateFlow<SettingsState>(SettingsState.Init)
    val state: StateFlow<SettingsState> get() = _state

    fun signOut() {
        authRepo.signOut()
        messagingRepo.unsubscribeFromFreeGamesTopic()
    }

    fun getUserName() = authRepo.getUserName() ?: ""
    fun getUserEmail() = authRepo.getUserEmail() ?: ""
    fun getUserPhotoUrl() = authRepo.getUserPhotoUrl()

    init {
        refreshUser()
    }

    fun refreshUser() {
        viewModelScope.launch {
            authRepo.reloadUser()
            val tempUser = authRepo.getCurrentUser()
            if (tempUser != null) {
                if (authRepo.isUserAuthenticated()) {
                    _state.value = SettingsState.IsAuthenticated(
                        tempUser.displayName ?: "",
                        tempUser.email ?: ""
                    )
                } else {
                    _state.value = SettingsState.IsError("User not logged in")
                }
            } else {
                _state.value = SettingsState.IsNotAuthenticated
            }
        }
    }
}

sealed class SettingsState {
    data object Init : SettingsState()
    data class IsAuthenticated(val displayName: String, val email: String) : SettingsState()
    data object IsNotAuthenticated : SettingsState()
    data class IsError(val message: String) : SettingsState()
}