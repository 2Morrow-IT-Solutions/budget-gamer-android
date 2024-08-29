package com.tomorrowit.budgetgamer.presentation.viewmodels.main_menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.budgetgamer.data.entities.SubscriptionWithGames
import com.tomorrowit.budgetgamer.domain.repo.room_repo.SubscriptionRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimedViewModel @Inject constructor(
    private val subscriptionRepo: SubscriptionRepo
) : ViewModel() {

    private val _state = MutableStateFlow<TimedState>(TimedState.Init)
    val state: StateFlow<TimedState> get() = _state

    init {
        _state.value = TimedState.IsLoading
        viewModelScope.launch {
            subscriptionRepo.getSubscriptionsWithGames(System.currentTimeMillis()).collect {
                if (it.isNotEmpty()) {
                    _state.value = TimedState.IsSuccess(it)
                }
            }
        }
    }
}

sealed class TimedState {
    data object Init : TimedState()
    data object IsLoading : TimedState()
    data class IsSuccess(val data: List<SubscriptionWithGames>) : TimedState()
    data class IsError(val message: String) : TimedState()
}