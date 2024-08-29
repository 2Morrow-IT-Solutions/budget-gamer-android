package com.tomorrowit.budgetgamer.common.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeUtility {

    companion object {
        @JvmStatic
        fun fromMillisecondsToDateStringDots(created: Long): String? {
            val formatterDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            return formatterDate.format(Date(created))
        }

        @JvmStatic
        fun fromMillisecondsToDateAndTimeStringDots(separator: String, created: Long): String? {
            val formatterDate =
                SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            return formatterDate.format(Date(created)).replace(" ", " $separator ")
        }
    }
}