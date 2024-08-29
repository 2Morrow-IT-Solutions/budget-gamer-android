package com.tomorrowit.budgetgamer.presentation.viewmodels.edit_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.AuthRepoResult
import com.tomorrowit.budgetgamer.domain.usecases.GetStringResourceForIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAccountViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val getStringResourceForIdUseCase: GetStringResourceForIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<EditAccountState>(EditAccountState.Init)
    val state: StateFlow<EditAccountState> get() = _state

    private var _isProvider = false
    private var _provider = ""

    var provider = authRepo.getProvider()

    init {
        val result = authRepo.isUserAuthenticated()
        if (result) {
            _provider = authRepo.getProvider()
            if (_provider == "google.com" || _provider == "apple.com") {
                _isProvider = true
            } else {
                _isProvider = false
            }
            refreshUser()
        } else {
            _state.value = EditAccountState.IsError("User not logged in")
        }
    }

    fun refreshUser() {
        viewModelScope.launch {
            authRepo.reloadUser()
            val tempUser = authRepo.getCurrentUser()
            if (tempUser != null) {
                if (_isProvider) {
                    _state.value = EditAccountState.IsAuthenticatedProvider(
                        _provider,
                        tempUser.displayName ?: "",
                        tempUser.email ?: ""
                    )
                } else {
                    _state.value = EditAccountState.IsAuthenticatedEmail(
                        tempUser.displayName ?: "",
                        tempUser.email ?: ""
                    )
                }
            } else {
                _state.value = EditAccountState.IsError("User not logged in")
            }
        }
    }

    fun deleteProviderAccount() {
        _state.value = EditAccountState.IsDeletingAccount
        viewModelScope.launch {
            val result = authRepo.deleteUser()
            if (result is AuthRepoResult.IsSuccess) {
                _state.value = EditAccountState.IsSuccessDeletingAccount
            } else {
                _state.value =
                    EditAccountState.IsErrorDeletingAccount(getStringResourceForIdUseCase.invoke(R.string.error_deleting_account))
            }
        }
    }
}

sealed class EditAccountState {
    data object Init : EditAccountState()
    data object IsLoading : EditAccountState()
    data class IsAuthenticatedProvider(
        val provider: String,
        val displayName: String,
        val email: String
    ) : EditAccountState()

    data class IsAuthenticatedEmail(val displayName: String, val email: String) : EditAccountState()
    data object IsDeletingAccount : EditAccountState()
    data object IsSuccessDeletingAccount : EditAccountState()
    data class IsErrorDeletingAccount(val message: String) : EditAccountState()
    data class IsError(val message: String) : EditAccountState()
}