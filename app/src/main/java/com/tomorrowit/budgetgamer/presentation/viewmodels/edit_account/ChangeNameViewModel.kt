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
class ChangeNameViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val getStringResourceForIdUseCase: GetStringResourceForIdUseCase,
    private val errorHelperUseCase: ErrorHelperUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<EditNameState>(EditNameState.Init)
    val state: StateFlow<EditNameState> get() = _state

    fun getName(): String {
        return authRepo.getCurrentUser()?.displayName.toString()
    }

    fun getErrorForField(string: String, fieldType: FieldType): String {
        return errorHelperUseCase.getErrorForField(string, fieldType)
    }

    fun changeName(newName: String) {
        _state.value = EditNameState.Init
        viewModelScope.launch {
            _state.value = EditNameState.IsLoading
            val result = authRepo.setUserName(newName)
            if (result is AuthRepoResult.IsSuccess) {
                _state.value = EditNameState.IsSuccess
            } else {
                _state.value =
                    EditNameState.ShowError(getStringResourceForIdUseCase.invoke(R.string.error_updating_name))
            }
        }
    }
}

sealed class EditNameState {
    data object Init : EditNameState()
    data object IsLoading : EditNameState()
    data object IsSuccess : EditNameState()
    data class ShowError(val message: String) : EditNameState()
}