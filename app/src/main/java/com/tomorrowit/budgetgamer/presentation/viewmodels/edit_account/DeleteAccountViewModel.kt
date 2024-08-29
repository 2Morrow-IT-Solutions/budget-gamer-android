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
class DeleteAccountViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val errorHelperUseCase: ErrorHelperUseCase,
    private val getStringResourceForIdUseCase: GetStringResourceForIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<DeleteState>(DeleteState.Init)
    val state: StateFlow<DeleteState> get() = _state

    fun getErrorForField(string: String, fieldType: FieldType): String {
        return errorHelperUseCase.getErrorForField(string, fieldType)
    }

    fun authenticateAndDelete(password: String) {
        _state.value = DeleteState.Init
        viewModelScope.launch {
            _state.value = DeleteState.IsLoading
            val result = authRepo.reAuthenticateUser(password)
            if (result is AuthRepoResult.IsSuccess) {
                deleteAccount()
            } else if (result is AuthRepoResult.IsFailure) {
                _state.value =
                    DeleteState.ShowError(errorHelperUseCase.firebaseLoginErrorToMessage(result.exception))
            }
        }
    }

    private suspend fun deleteAccount() {
        val result = authRepo.deleteUser()
        if (result is AuthRepoResult.IsSuccess) {
            _state.value = DeleteState.IsSuccess
        } else {
            _state.value =
                DeleteState.ShowError(getStringResourceForIdUseCase.invoke(R.string.error_deleting_account))
        }
    }
}

sealed class DeleteState {
    data object Init : DeleteState()
    data object IsLoading : DeleteState()
    data object IsSuccess : DeleteState()
    data class ShowError(val message: String) : DeleteState()
}
