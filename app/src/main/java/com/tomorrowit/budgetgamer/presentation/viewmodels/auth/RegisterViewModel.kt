package com.tomorrowit.budgetgamer.presentation.viewmodels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.budgetgamer.domain.listeners.FieldType
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.AuthRepoResult
import com.tomorrowit.budgetgamer.domain.repo.MessagingRepo
import com.tomorrowit.budgetgamer.domain.repo.PreferencesRepo
import com.tomorrowit.budgetgamer.domain.usecases.ErrorHelperUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val errorHelperUseCase: ErrorHelperUseCase,
    private val messagingRepo: MessagingRepo,
    private val preferencesRepo: PreferencesRepo
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Init)
    val state: StateFlow<RegisterState> get() = _state

    fun getErrorForField(string: String, fieldType: FieldType): String {
        return errorHelperUseCase.getErrorForField(string, fieldType)
    }

    private fun getUid(): String {
        return authRepo.getCurrentUserUid() ?: ""
    }

    private fun subscribeToTopic() {
        messagingRepo.subscribeToFreeGamesTopic()
    }

    private fun unsubscribeToTopic() {
        messagingRepo.unsubscribeFromFreeGamesTopic()
    }

    private suspend fun getNotificationValue(): String {
        return preferencesRepo.getMessagingToken().first()
    }

    fun register(name: String, email: String, password: String) {
        _state.value = RegisterState.IsLoading
        viewModelScope.launch {
            val result = authRepo.createUser(email, password)
            if (result is AuthRepoResult.IsSuccess) {
                setName(name)
            } else if (result is AuthRepoResult.IsFailure) {
                _state.value =
                    RegisterState.ShowError(
                        errorHelperUseCase.firebaseLoginErrorToMessageSecret(
                            result.exception
                        )
                    )
            }
        }
    }

    private suspend fun setName(name: String) {
        val result = authRepo.setUserName(name)
        if (result is AuthRepoResult.IsSuccess) {
            _state.value = RegisterState.IsSuccess
        } else if (result is AuthRepoResult.IsFailure) {
            _state.value =
                RegisterState.ShowError(errorHelperUseCase.firebaseLoginErrorToMessageSecret(result.exception))
        }
    }

    suspend fun checkNotifications() {
        if (getNotificationValue() == (getUid()) + "free_games") {
            subscribeToTopic()
        } else {
            unsubscribeToTopic()
        }
    }
}

sealed class RegisterState {
    data object Init : RegisterState()
    data object IsLoading : RegisterState()
    data object IsSuccess : RegisterState()
    data class ShowError(val message: String) : RegisterState()
}