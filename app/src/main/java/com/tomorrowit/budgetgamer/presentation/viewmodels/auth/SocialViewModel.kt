package com.tomorrowit.budgetgamer.presentation.viewmodels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
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
class SocialViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val getStringResourceForIdUseCase: GetStringResourceForIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SocialState>(SocialState.Init)
    val state: StateFlow<SocialState> get() = _state

    suspend fun authForGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            val result = authRepo.signIn(credential)
            if (result is AuthRepoResult.IsSuccess) {
                _state.value = SocialState.IsSuccess
            } else {
                _state.value =
                    SocialState.ShowError(getStringResourceForIdUseCase.invoke(R.string.sign_in_google_failure))
            }
        }
    }
}

sealed class SocialState {
    data object Init : SocialState()
    data object IsSuccess : SocialState()
    data class ShowError(val message: String) : SocialState()
}