package com.tomorrowit.budgetgamer.domain.usecases.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi

class GetNotificationChannelsUseCase(
    private val context: Context
) {
    companion object {
        const val NOTIFICATION_CHANNEL_FREE_GAMES = "free_games_channel"

        //Todo: Later add resources for these ones:
        const val CHANNEL_1_NAME = "Free games notifications"
        const val CHANNEL_1_DESCRIPTION =
            "This is the channel that sends notifications whenever there's a new free game."
    }

    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(): List<NotificationChannel> {
        return listOf(
            NotificationChannel(
                NOTIFICATION_CHANNEL_FREE_GAMES,
                CHANNEL_1_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_1_DESCRIPTION
                enableVibration(true)
                enableLights(true)
                lightColor = Color.MAGENTA
            }
        )
    }
}