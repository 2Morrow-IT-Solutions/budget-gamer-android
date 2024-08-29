package com.tomorrowit.budgetgamer.domain.usecases.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri

class OpenUrlAddressUseCase(
    private val activity: Activity
) {

    operator fun invoke(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        activity.startActivity(intent)
    }
}