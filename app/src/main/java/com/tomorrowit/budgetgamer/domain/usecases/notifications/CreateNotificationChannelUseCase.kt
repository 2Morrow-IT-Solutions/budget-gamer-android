package com.tomorrowit.budgetgamer.domain.usecases.notifications

import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.tomorrowit.budgetgamer.common.config.extensions.tag

class CreateNotificationChannelUseCase(
    private val notificationManager: NotificationManager,
    private val getNotificationChannelsUseCase: GetNotificationChannelsUseCase
) {
    operator fun invoke() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannels(getNotificationChannelsUseCase.invoke())
        } else {
            Log.i(tag(), "Skipping notification channel creation. OS below Android Oreo.")
        }
    }
}