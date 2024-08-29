package com.tomorrowit.budgetgamer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.tomorrowit.budgetgamer.domain.usecases.GetHasInternetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class BlockerViewModel @Inject constructor(
    private val getHasInternetUseCase: GetHasInternetUseCase,
) : ViewModel() {
    val hasInternet: StateFlow<Boolean> get() = getHasInternetUseCase.invoke()

}