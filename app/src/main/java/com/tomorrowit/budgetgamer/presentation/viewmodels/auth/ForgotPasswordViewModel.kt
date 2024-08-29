package com.tomorrowit.budgetgamer.presentation.viewmodels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.budgetgamer.domain.listeners.FieldType
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.AuthRepoResult
import com.tomorrowit.budgetgamer.domain.usecases.ErrorHelperUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val errorHelperUseCase: ErrorHelperUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ForgotState>(ForgotState.Init)
    val state: StateFlow<ForgotState> get() = _state

    fun getErrorForField(string: String, fieldType: FieldType): String {
        return errorHelperUseCase.getErrorForField(string, fieldType)
    }

    fun forgot(email: String) {
        _state.value = ForgotState.IsLoading
        viewModelScope.launch {
            val result = authRepo.resetPassword(email)
            if (result is AuthRepoResult.IsSuccess) {
                _state.value = ForgotState.IsSuccess
            } else if (result is AuthRepoResult.IsFailure) {
                _state.value =
                    ForgotState.ShowError(
                        errorHelperUseCase.firebaseLoginErrorToMessageSecret(
                            result.exception
                        )
                    )
            }
        }
    }
}

sealed class ForgotState {
    data object Init : ForgotState()
    data object IsLoading : ForgotState()
    data object IsSuccess : ForgotState()
    data class ShowError(val message: String) : ForgotState()
}