package com.tomorrowit.budgetgamer.domain.usecases.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class IsPermissionGivenUseCase(
    private val context: Context
) {

    operator fun invoke(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED
}