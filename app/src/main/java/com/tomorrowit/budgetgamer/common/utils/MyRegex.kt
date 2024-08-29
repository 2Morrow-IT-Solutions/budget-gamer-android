package com.tomorrowit.budgetgamer.common.utils

import org.intellij.lang.annotations.RegExp
import java.util.regex.Pattern

object MyRegex {

    @RegExp
    val steamUrl: Pattern = Pattern.compile("store\\.steampowered\\.com\\/app\\/[0-9]+\\/")

    @RegExp
    val epicGamesUrl: Pattern =
        Pattern.compile("store\\.epicgames\\.com\\/([a-zA-Z]+(-[a-zA-Z]+))+\\/.*\\/")

    @RegExp
    val xboxUrl: Pattern =
        Pattern.compile("xbox\\.com\\/([a-zA-Z]+(-[a-zA-Z]+)+)\\/games\\/store\\/.*")

    @RegExp
    val playstationUrl: Pattern =
        Pattern.compile("store\\.playstation\\.com\\/([a-zA-Z]+(-[a-zA-Z]+)+)\\/product\\/.*")

    @RegExp
    val humbleBundleUrl: Pattern = Pattern.compile("humblebundle\\.com\\/store\\/.*")

    @RegExp
    val gogUrl: Pattern = Pattern.compile("gog\\.com\\/.*\\/game\\/.*")

    @RegExp
    val gogUrlAlt: Pattern = Pattern.compile("\"gog\\.com\\/game\\/.*")

}