package com.tomorrowit.budgetgamer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.budgetgamer.BuildConfig
import com.tomorrowit.budgetgamer.common.utils.Logic
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.MessagingRepo
import com.tomorrowit.budgetgamer.domain.repo.PreferencesRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ClientPlatformRepo
import com.tomorrowit.budgetgamer.domain.usecases.sync.SyncPlatformSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val syncPlatformSettings: SyncPlatformSettings,
    private val clientPlatformRepo: ClientPlatformRepo,
    private val messagingRepo: MessagingRepo,
    private val preferencesRepo: PreferencesRepo
) : ViewModel() {

    private var _state = MutableStateFlow<AuthState>(AuthState.Init)
    val state: StateFlow<AuthState> get() = _state

    init {
        _state.value = AuthState.IsLoading
        viewModelScope.launch {
            syncPlatformSettings.invoke()
        }
        viewModelScope.launch {
            syncPlatformSettings.firstSyncDone.collect {
                if (it) {
                    getAppState()
                    this.cancel()
                }
            }
        }
    }

    private fun getAppState() {
        viewModelScope.launch {
            clientPlatformRepo.getClientPlatformById("android").collect { client ->
                if (client != null) {
                    if (!client.maintenance && !shouldUpdate(client.minVersion)) {
                        _state.value = AuthState.IsSuccess
                    } else {
                        if (shouldUpdate(client.minVersion)) {
                            _state.value = AuthState.IsUpdateRequired
                        } else if (client.maintenance) {
                            _state.value = AuthState.IsMaintenance
                        }
                    }
                } else {
                    _state.value = AuthState.IsError("Something happened")
                }
            }
        }
    }

    private fun subscribeToTopic() {
        messagingRepo.subscribeToFreeGamesTopic()
    }

    private fun unsubscribeToTopic() {
        messagingRepo.unsubscribeFromFreeGamesTopic()
    }

    private suspend fun getNotificationValue(): String {
        return preferencesRepo.getMessagingToken().first()
    }

    private fun getUid(): String {
        return authRepo.getCurrentUserUid() ?: ""
    }

    private fun shouldUpdate(minVersion: String): Boolean {
        return Logic.getIntegerForAppVersion(BuildConfig.VERSION_NAME) < Logic.getIntegerForAppVersion(
            minVersion
        )
    }

    suspend fun checkNotifications() {
        if (getNotificationValue() == (getUid()) + "free_games") {
            subscribeToTopic()
        } else {
            unsubscribeToTopic()
        }
    }
}

sealed class AuthState {
    data object Init : AuthState()
    data object IsLoading : AuthState()
    data object IsSuccess : AuthState()
    data object IsMaintenance : AuthState()
    data object IsUpdateRequired : AuthState()
    data class IsError(val message: String) : AuthState()
}