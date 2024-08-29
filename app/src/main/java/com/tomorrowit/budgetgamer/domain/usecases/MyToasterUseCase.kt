package com.tomorrowit.budgetgamer.domain.usecases

import android.content.Context
import android.widget.Toast
import com.tomorrowit.budgetgamer.BuildConfig

class MyToasterUseCase(private val context: Context) {

    private val LENGTH_SHORT: Int by lazy {
        Toast.LENGTH_SHORT
    }
    private val LENGTH_LONG: Int by lazy {
        Toast.LENGTH_LONG
    }

    operator fun invoke(
        text: String,
        isDebug: Boolean = false,
        isLong: Boolean = false
    ) {
        val toastLength = if (isLong) LENGTH_LONG else LENGTH_SHORT
        if (isDebug) {
            debug(text, toastLength)
        } else {
            normal(text, toastLength)
        }
    }

    private fun debug(text: String, toastLength: Int = LENGTH_LONG) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, "DEBUG: $text", toastLength).show()
        }
    }

    private fun normal(text: String, toastLength: Int = LENGTH_SHORT) {
        Toast.makeText(context, text, toastLength).show()
    }
}