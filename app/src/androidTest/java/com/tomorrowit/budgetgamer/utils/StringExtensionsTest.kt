package com.tomorrowit.budgetgamer.utils

import com.tomorrowit.budgetgamer.common.config.extensions.isGameUrl
import com.tomorrowit.budgetgamer.common.config.extensions.isUrl
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun given_supported_url_isUrl_returns_true() {
        assert("https://www.google.com/".isUrl())
    }

    @Test
    fun given_supported_game_url_isGameUrl_returns_true() {
        assert("https://store.steampowered.com/app/1091500/Cyberpunk_2077/".isGameUrl())
    }
}