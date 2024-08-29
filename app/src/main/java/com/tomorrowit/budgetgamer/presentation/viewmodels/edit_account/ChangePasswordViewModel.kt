package com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.domain.listeners.FieldType
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.AuthRepoResult
import com.tomorrowit.budgetgamer.domain.usecases.ErrorHelperUseCase
import com.tomorrowit.budgetgamer.domain.usecases.GetStringResourceForIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val getStringResourceForIdUseCase: GetStringResourceForIdUseCase,
    private val errorHelperUseCase: ErrorHelperUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Init)
    val state: StateFlow<ChangePasswordState> get() = _state

    fun getErrorForField(string: String, fieldType: FieldType): String {
        return errorHelperUseCase.getErrorForField(string, fieldType)
    }

    fun authenticateAndEditPassword(oldPassword: String, newPassword: String) {
        _state.value = ChangePasswordState.Init
        viewModelScope.launch {
            _state.value = ChangePasswordState.IsLoading
            val result = authRepo.reAuthenticateUser(oldPassword)
            if (result is AuthRepoResult.IsSuccess) {
                editPasswordAccount(newPassword)
            } else if (result is AuthRepoResult.IsFailure) {
                _state.value = ChangePasswordState.ShowError(result.exception.message.toString())
            }
        }
    }

    private suspend fun editPasswordAccount(newPassword: String) {
        val result = authRepo.changePassword(newPassword)
        if (result is AuthRepoResult.IsSuccess) {
            _state.value = ChangePasswordState.IsSuccess
        } else {
            _state.value =
                ChangePasswordState.ShowError(getStringResourceForIdUseCase.invoke(R.string.error_updating_password))
        }
    }
}

sealed class ChangePasswordState {
    data object Init : ChangePasswordState()
    data object IsLoading : ChangePasswordState()
    data object IsSuccess : ChangePasswordState()
    data class ShowError(val message: String) : ChangePasswordState()
}