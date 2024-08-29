package com.tomorrowit.budgetgamer.common.config.extensions

import android.webkit.URLUtil
import com.tomorrowit.budgetgamer.common.utils.MyRegex
import java.util.regex.Pattern

fun String.isValidEmail(): Boolean {
    val emailAddressPattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    return emailAddressPattern.matcher(this).matches()
}

fun String.isValidName(): Boolean = this.length in 3..99

fun String.isValidPassword(): Boolean {
    var is8char = false
    var is100char = false
    var hasUpper = false
    var hasLower = false
    var hasNum = false
    var hasSpecialSymbol = false

    // 8 character
    if (this.length >= 8) {
        is8char = true
    }

    // 100 character
    if (this.length < 100) {
        is100char = true
    }

    //number
    if (this.matches("(.*[0-9].*)".toRegex())) {
        hasNum = true
    }

    //upper case
    if (this.matches("(.*[A-Z].*)".toRegex())) {
        hasUpper = true
    }

    //lower case
    if (this.matches("(.*[a-z].*)".toRegex())) {
        hasLower = true
    }

    //symbol
    if (this.matches("^(?=.*[_,.()$#!?&@]).*$".toRegex())) {
        hasSpecialSymbol = true
    }

    return (is8char && hasUpper && hasLower && hasNum && is100char && hasSpecialSymbol)
}

fun String.isValidField(): Boolean {
    return this.isNotEmpty()
}

fun String.isUrl(): Boolean {
    return URLUtil.isValidUrl(this)
}

fun String.isGameUrl(): Boolean {
    if (this.isUrl()) {
        if (MyRegex.steamUrl.matcher(this).find()) {
            return true
        }
        if (MyRegex.epicGamesUrl.matcher(this).find()) {
            return true
        }
        if (MyRegex.xboxUrl.matcher(this).find()) {
            return true
        }
        if (MyRegex.playstationUrl.matcher(this).find()) {
            return true
        }
        if (MyRegex.humbleBundleUrl.matcher(this).find()) {
            return true
        }
        if (MyRegex.gogUrl.matcher(this).find()) {
            return true
        }
        if (MyRegex.gogUrlAlt.matcher(this).find()) {
            return true
        }
    }
    return false
}