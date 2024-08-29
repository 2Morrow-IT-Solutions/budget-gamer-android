package com.tomorrowit.budgetgamer.domain.usecases.notifications

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class ShowNotificationUseCase(
    private val notificationManagerCompat: NotificationManagerCompat
) {
    //@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    operator fun invoke(
        context: Context,
        notificationId: Int,
        notification: Notification,
        notificationTag: String? = null
    ) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManagerCompat.notify(
                notificationTag,
                notificationId,
                notification
            )
        }
    }
}