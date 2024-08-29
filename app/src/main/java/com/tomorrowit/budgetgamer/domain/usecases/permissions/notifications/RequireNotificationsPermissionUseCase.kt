package com.tomorrowit.budgetgamer.domain.usecases.permissions.notifications

import android.app.Activity
import android.os.Build
import androidx.fragment.app.Fragment
import com.tomorrowit.budgetgamer.domain.usecases.permissions.RequirePermissionsUseCase


class RequireNotificationsPermissionUseCase(
    private val requirePermissionsUseCase: RequirePermissionsUseCase
) {
    operator fun invoke(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requirePermissionsUseCase.invoke(
                activity,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    operator fun invoke(fragment: Fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requirePermissionsUseCase.invoke(
                fragment,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }
}