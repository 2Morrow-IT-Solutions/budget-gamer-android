package com.tomorrowit.budgetgamer.common.network

import android.content.Context
import com.google.firebase.FirebaseApp
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import com.tomorrowit.budgetgamer.domain.usecases.notifications.CreateNotificationChannelUseCase

class AppInitializer(
    private val context: Context,
    private val authRepo: AuthRepo,
    private val loggerRepo: LoggerRepo,
    private val createNotificationChannelUseCase: CreateNotificationChannelUseCase
) {
    fun initialize() {
        FirebaseApp.initializeApp(context)
        authRepo.setLanguage()
        authRepo.getCurrentUserUid()?.let {
            loggerRepo.setUid(it)
        }
        createNotificationChannelUseCase.invoke()
    }
}