package com.tomorrowit.budgetgamer.common.services

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tomorrowit.budgetgamer.common.config.extensions.tag
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import com.tomorrowit.budgetgamer.domain.repo.MessagingRepo
import com.tomorrowit.budgetgamer.domain.repo.PreferencesRepo
import com.tomorrowit.budgetgamer.domain.usecases.notifications.CreateNotificationUseCase
import com.tomorrowit.budgetgamer.domain.usecases.notifications.ShowNotificationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var preferencesRepo: PreferencesRepo

    @Inject
    lateinit var authRepo: AuthRepo

    @Inject
    lateinit var messagingRepo: MessagingRepo

    @Inject
    lateinit var showNotificationUseCase: ShowNotificationUseCase

    @Inject
    lateinit var createNotificationUseCase: CreateNotificationUseCase

    @Inject
    lateinit var loggerRepo: LoggerRepo

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        loggerRepo.debug(tag(), "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            loggerRepo.debug(tag(), "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            loggerRepo.debug(tag(), "Message Notification Body: ${it.body}")

            if (ContextCompat.checkSelfPermission(
                    this@MyFirebaseMessagingService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                showNotificationUseCase.invoke(
                    this@MyFirebaseMessagingService,
                    //Todo: Decide on how to show the notification!!!
                    0,
                    createNotificationUseCase.invoke(
                        CreateNotificationUseCase.NotificationType.FREE_GAME_NOTIFICATION,
                        getString(
                            getStringResourceByName(remoteMessage.notification!!.titleLocalizationKey!!),
                            remoteMessage.notification!!.titleLocalizationArgs?.get(0)
                        ),
                        getString(
                            getStringResourceByName(remoteMessage.notification!!.bodyLocalizationKey!!),
                            remoteMessage.notification!!.bodyLocalizationArgs?.get(0)
                        )
                    )
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        loggerRepo.debug(tag(), "Refreshed token: $token")

        if (authRepo.isUserAuthenticated()) {
            scope.launch {
                if (token != preferencesRepo.getMessagingToken().first()) {
                    messagingRepo.subscribeToFreeGamesTopic()
                } else {
                    messagingRepo.unsubscribeFromFreeGamesTopic()
                }
            }
        }
    }

    private fun getStringResourceByName(aString: String?): Int {
        //For notification_title as a string, get the int ID of it from resources basically
        val packageName = packageName
        return resources.getIdentifier(aString, "string", packageName)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}