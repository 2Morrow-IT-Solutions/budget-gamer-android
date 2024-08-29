package com.tomorrowit.budgetgamer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.ReadRepo
import com.tomorrowit.budgetgamer.domain.repo.ReadResponse
import com.tomorrowit.budgetgamer.domain.usecases.ClearOldDataUseCase
import com.tomorrowit.budgetgamer.domain.usecases.GetHasInternetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getHasInternetUseCase: GetHasInternetUseCase,
    private val authRepo: AuthRepo,
    private val readRepo: ReadRepo,
    private val clearOldDataUseCase: ClearOldDataUseCase
) : ViewModel() {
    val hasInternet: StateFlow<Boolean> get() = getHasInternetUseCase.invoke()

    private var _showBanner: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showBanner: StateFlow<Boolean> get() = _showBanner

    private var _userBanned: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val userBanned: StateFlow<Boolean> get() = _userBanned

    init {
        _showBanner.value = !authRepo.isUserAuthenticated()
        viewModelScope.launch {
            readRepo.getIsMyUserBanned().distinctUntilChanged().collectLatest {
                if (it is ReadResponse.IsSuccess) {
                    _userBanned.value = it.data
                } else {
                    _userBanned.value = true
                }
            }
        }
        viewModelScope.launch {
            clearOldDataUseCase.invoke()
        }
    }

    fun isUserAuthenticated(): Boolean {
        return authRepo.isUserAuthenticated()
    }

    fun dismissBanner() {
        _showBanner.value = false
    }

    override fun onCleared() {
        super.onCleared()
    }
}

sealed class MainState() {
    data object Init : MainState()
}