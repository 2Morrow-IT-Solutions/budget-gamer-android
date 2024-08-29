package com.tomorrowit.budgetgamer.domain.usecases.permissions

import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class RequirePermissionsUseCase {
    private val REQUEST_CODE: Int = 500
    operator fun invoke(activity: Activity, vararg permission: String) {
        ActivityCompat.requestPermissions(
            activity,
            permission,
            REQUEST_CODE
        )
    }

    operator fun invoke(fragment: Fragment, vararg permission: String) {
        fragment.requestPermissions(permission, REQUEST_CODE)
    }
}