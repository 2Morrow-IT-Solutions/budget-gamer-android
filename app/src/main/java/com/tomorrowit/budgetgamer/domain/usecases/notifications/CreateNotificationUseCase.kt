package com.tomorrowit.budgetgamer.domain.usecases.notifications

import android.app.Notification
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.tomorrowit.budgetgamer.R

class CreateNotificationUseCase(private val context: Context) {
    enum class NotificationType {
        FREE_GAME_NOTIFICATION
    }

    private val purpleColor: Int by lazy {
        Color.parseColor("#694BCE")
    }

    operator fun invoke(
        notificationType: NotificationType,
        title: String = "Empty title",
        description: String = "Empty description"
    ): Notification {
        val notification: Notification = when (notificationType) {
            NotificationType.FREE_GAME_NOTIFICATION -> {
                NotificationCompat.Builder(
                    context,
                    GetNotificationChannelsUseCase.NOTIFICATION_CHANNEL_FREE_GAMES
                )
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                    .setLights(Color.MAGENTA, 3000, 500)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setColor(purpleColor)
                    .setColorized(true)
                    .setAutoCancel(true)
                    .build()
            }
        }
        return notification
    }
}