package com.tomorrowit.budgetgamer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.MessagingRepo
import com.tomorrowit.budgetgamer.domain.repo.PreferencesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val messagingRepo: MessagingRepo,
    private val preferencesRepo: PreferencesRepo
) : ViewModel() {

    fun subscribeToTopic() {
        messagingRepo.subscribeToFreeGamesTopic()
    }

    fun unsubscribeToTopic() {
        messagingRepo.unsubscribeFromFreeGamesTopic()
    }

    suspend fun getNotificationValue(): String {
        return preferencesRepo.getMessagingToken().first()
    }

    suspend fun setNotificationValue(value: String) {
        return preferencesRepo.setMessagingToken(value)
    }

    fun getUid(): String {
        return authRepo.getCurrentUserUid() ?: ""
    }

}