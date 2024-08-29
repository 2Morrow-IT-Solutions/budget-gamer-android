package com.tomorrowit.budgetgamer.common.utils

import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import com.tomorrowit.budgetgamer.R
import java.util.Locale

object Logic {

    @JvmStatic
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    @JvmStatic
    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    @JvmStatic
    fun getProviderDrawableForString(code: String): Int {
        return when (code.lowercase().trim()) {
            "logo_humble_bundle" -> R.drawable.logo_humble_bundle
            "logo_origin" -> R.drawable.logo_origin
            "logo_gog" -> R.drawable.logo_gog
            "logo_epic_games" -> R.drawable.logo_epic_games
            "logo_xbox" -> R.drawable.logo_xbox
            "logo_playstation" -> R.drawable.logo_playstation
            "logo_steam" -> R.drawable.logo_steam
            "logo_amazon_games" -> R.drawable.logo_amazon_games
            else -> R.drawable.logo_steam
        }
    }

    @JvmStatic
    fun getPlatformDrawableForString(code: String): Int {
        return when (code.lowercase().trim()) {
            "logo_linux" -> R.drawable.logo_linux
            "logo_mac_os" -> R.drawable.logo_mac_os
            "logo_nintendo_switch" -> R.drawable.logo_nintendo_switch
            "logo_ps5" -> R.drawable.logo_ps5
            "logo_ps4" -> R.drawable.logo_ps4
            "logo_xbox_classic" -> R.drawable.logo_xbox_classic
            "logo_xbox_360" -> R.drawable.logo_xbox_360
            "logo_xbox_one" -> R.drawable.logo_xbox_one
            "logo_xbox_series" -> R.drawable.logo_xbox_series
            "logo_windows" -> R.drawable.logo_windows
            else -> R.drawable.logo_windows
        }
    }

    @JvmStatic
    fun getColourForString(code: String): Int {
        return Color.parseColor(code)
    }

    @JvmStatic
    fun getInitialsFromName(displayName: String?): String {
        if (displayName.isNullOrBlank()) return ""

        val names = displayName.trim().split(" ").filter { it.isNotEmpty() }

        return when (names.size) {
            0 -> ""
            1 -> {
                val firstChar = firstChar(names[0])
                val secondChar = if (names[0].length > 1) names[0][1].toString() else ""
                firstChar + secondChar
            }

            3 -> firstChar(names[0]) + firstChar(names[2])
            else -> firstChar(names[0]) + firstChar(names[1])
        }
    }

    private fun firstChar(name: String): String {
        return if (name.isNotEmpty()) name[0].toString().uppercase() else ""
    }

    @JvmStatic
    fun getPhoneLanguageCode(): String {
        return Locale.getDefault().language
    }

    @JvmStatic
    fun getIntegerForAppVersion(version: String): Int {
        return version.replace(".", "").toInt()
    }
}