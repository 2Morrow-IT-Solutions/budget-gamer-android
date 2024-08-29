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
class ChangeEmailViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val getStringResourceForIdUseCase: GetStringResourceForIdUseCase,
    private val errorHelperUseCase: ErrorHelperUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<EditEmailState>(EditEmailState.Init)
    val state: StateFlow<EditEmailState> get() = _state

    fun getEmail(): String {
        return authRepo.getCurrentUser()?.email.toString()
    }

    fun getErrorForField(string: String, fieldType: FieldType): String {
        return errorHelperUseCase.getErrorForField(string, fieldType)
    }

    private fun changeEmail(newEmail: String) {
        viewModelScope.launch {
            val result = authRepo.changeEmail(newEmail)
            if (result is AuthRepoResult.IsSuccess) {
                _state.value = EditEmailState.IsSuccess
            } else {
                _state.value =
                    EditEmailState.ShowError(getStringResourceForIdUseCase.invoke(R.string.error_updating_email))
            }
        }
    }

    fun authenticateAndChangeEmail(newEmail: String, password: String) {
        _state.value = EditEmailState.Init
        viewModelScope.launch {
            _state.value = EditEmailState.IsLoading
            val result = authRepo.reAuthenticateUser(password)
            if (result is AuthRepoResult.IsSuccess) {
                changeEmail(newEmail)
            } else if (result is AuthRepoResult.IsFailure) {
                _state.value =
                    EditEmailState.ShowError(errorHelperUseCase.firebaseLoginErrorToMessage(result.exception))
            }
        }
    }
}

sealed class EditEmailState {
    data object Init : EditEmailState()
    data object IsLoading : EditEmailState()
    data object IsSuccess : EditEmailState()
    data class ShowError(val message: String) : EditEmailState()
}