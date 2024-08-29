package com.tomorrowit.budgetgamer.domain.usecases.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.config.Constants.AppInfo.APP_STORE_URL
import com.tomorrowit.budgetgamer.common.config.Constants.AppInfo.CONTACT_EMAIL
import com.tomorrowit.budgetgamer.common.config.Constants.AppInfo.PLAY_STORE_URL
import com.tomorrowit.budgetgamer.domain.usecases.MyToasterUseCase

class OpenApplicationSettingsUseCase(
    private val activity: Activity,
    private val myToasterUseCase: MyToasterUseCase
) {

    companion object {
        const val APP_SETTINGS = 0
        const val NOTIFICATION_SETTINGS = 1
        const val SHARE_APP = 2
        const val SEND_EMAIL = 3
    }

    operator fun invoke(settingsPage: Int = 0) {
        when (settingsPage) {
            NOTIFICATION_SETTINGS -> {
                activity.startActivity(applicationNotificationsSettingsIntent)
            }

            SHARE_APP -> {
                activity.startActivity(shareAppIntent)
            }

            SEND_EMAIL -> {
                if (sendEmailIntent.resolveActivity(activity.packageManager) != null) {
                    activity.startActivity(sendEmailIntent)
                } else {
                    myToasterUseCase.invoke(activity.getString(R.string.error_no_email_app))
                }
            }

            else -> {
                activity.startActivity(generalSettingsIntent)
            }
        }
    }

    //region Intents
    private val applicationNotificationsSettingsIntent: Intent by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).also {
                it.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                it.data = Uri.parse("package:${activity.packageName}")
            }
        }.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private val generalSettingsIntent: Intent by lazy {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${activity.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private val shareAppIntent: Intent by lazy {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(
                Intent.EXTRA_TEXT,
                activity.resources.getString(R.string.share_app_message) + "\nGoogle Play: $PLAY_STORE_URL" + "\nApp Store: $APP_STORE_URL"
            )
            type = "text/plain"
        }
        Intent.createChooser(sendIntent, null)
    }

    private val sendEmailIntent: Intent by lazy {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, Array(1) { CONTACT_EMAIL })
            putExtra(
                Intent.EXTRA_SUBJECT,
                "Feedback on: ${activity.applicationInfo.loadLabel(activity.packageManager)}"
            )
        }
    }
    //endregion
}