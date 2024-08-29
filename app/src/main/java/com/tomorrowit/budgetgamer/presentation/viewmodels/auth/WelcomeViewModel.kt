package com.tomorrowit.budgetgamer.presentation.viewmodels.auth

import androidx.lifecycle.ViewModel
import com.tomorrowit.budgetgamer.domain.usecases.IsGoogleServicesAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val isGoogleServicesAvailable: IsGoogleServicesAvailable
) : ViewModel() {
    val googleServicesAvailable: Flow<Boolean> get() = isGoogleServicesAvailable.invoke()
}