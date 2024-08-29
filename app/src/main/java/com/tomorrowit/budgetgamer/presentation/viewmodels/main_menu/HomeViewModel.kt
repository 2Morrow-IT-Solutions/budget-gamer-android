package com.tomorrowit.budgetgamer.presentation.viewmodels.main_menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.budgetgamer.data.entities.GameWithProvider
import com.tomorrowit.budgetgamer.domain.repo.room_repo.GameRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameRepo: GameRepo
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.Init)
    val state: StateFlow<HomeState> get() = _state

    init {
        _state.value = HomeState.IsLoading
        viewModelScope.launch {
            gameRepo.getAllActiveGames(System.currentTimeMillis()).collect {
                if (it.isNotEmpty()) {
                    _state.value = HomeState.IsSuccess(it)
                } else {
                    _state.value = HomeState.IsEmpty
                }
            }
        }
    }
}

sealed class HomeState {
    data object Init : HomeState()
    data object IsLoading : HomeState()
    data class IsSuccess(val data: List<GameWithProvider>) : HomeState()
    data object IsEmpty : HomeState()
    data class IsError(val message: String) : HomeState()
}