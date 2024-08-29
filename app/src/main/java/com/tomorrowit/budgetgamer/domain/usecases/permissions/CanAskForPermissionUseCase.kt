package com.tomorrowit.budgetgamer.domain.usecases.permissions

import android.app.Activity
import androidx.core.app.ActivityCompat

class CanAskForPermissionUseCase {
    operator fun invoke(activity: Activity, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return false
            }
        }
        return true
    }
}
