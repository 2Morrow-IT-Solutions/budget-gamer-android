package com.tomorrowit.budgetgamer.domain.usecases.permissions.notifications

import android.os.Build
import com.tomorrowit.budgetgamer.domain.usecases.permissions.IsPermissionGivenUseCase

class IsNotificationsPermissionGivenUseCase(
    private val isPermissionGivenUseCase: IsPermissionGivenUseCase
) {
    operator fun invoke(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return isPermissionGivenUseCase.invoke(
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            true
        }
    }
}