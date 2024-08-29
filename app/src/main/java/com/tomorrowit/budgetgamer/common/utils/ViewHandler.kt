package com.tomorrowit.budgetgamer.common.utils

import android.content.Context
import android.content.res.Configuration
import android.widget.ImageView
import android.widget.TextView

object ViewHandler {

    @JvmStatic
    fun toggleChevronRotation(view: ImageView) {
        if (view.rotation == 0f) {
            view.rotation = -90f
        } else {
            view.rotation = 0f
        }
    }

    @JvmStatic
    fun toggleTextValues(view: TextView, text1: String, text2: String) {
        if (view.text.toString() == text1) {
            view.text = text2
        } else {
            view.text = text1
        }
    }

    @JvmStatic
    fun isDarkModeOn(context: Context): Boolean {
        val value: Int =
            context.applicationContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return value == Configuration.UI_MODE_NIGHT_YES
    }
}