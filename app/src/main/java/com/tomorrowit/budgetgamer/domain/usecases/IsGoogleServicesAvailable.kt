package com.tomorrowit.budgetgamer.domain.usecases

import android.content.pm.PackageManager
import com.tomorrowit.budgetgamer.common.config.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IsGoogleServicesAvailable(
    private val packageManager: PackageManager
) {

    operator fun invoke(): Flow<Boolean> = flow {
        val isAvailable = try {
            packageManager.getApplicationInfo(Constants.GOOGLE_PLAY_SERVICES_PACKAGE_ID, 0).enabled
        } catch (e: Exception) {
            false
        }
        emit(isAvailable)
    }
}