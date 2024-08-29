package com.tomorrowit.budgetgamer.common.config

import com.tomorrowit.budgetgamer.BuildConfig

object Constants {

    object URL {
        const val PLAY_STORE: String =
            "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        const val APP_STORE: String = "https://apps.apple.com/app/budget-gamer/id6443675890"
        const val DISCORD: String = "https://discord.gg/E8CY348Y"
        const val GITHUB: String = "https://github.com/2Morrow-IT-Solutions/budget-gamer-android"
        const val LINKEDIN: String = "https://www.linkedin.com/company/2morrow-it/"
    }

    object FirebaseDatabase {
        const val ARTICLE_LINKS: String = "article_links"
        const val GAME_LINKS: String = "game_links"
        const val ACCEPTED_LINKS: String = "accepted_links"
        const val DENIED_LINKS: String = "denied_links"
    }

    object AppInfo {
        const val CONTACT_EMAIL: String = "contact@budgetgamer.app"
        const val PLAY_STORE_URL: String =
            "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        const val APP_STORE_URL: String = "https://apps.apple.com/app/budget-gamer/id6443675890"
    }

    object Preferences {
        const val NOTIFICATION_PREFERENCE_KEY: String = "NOTIFICATION_PREFERENCE_KEY"

    }

    val GOOGLE_PLAY_SERVICES_PACKAGE_ID = "com.google.android.gms"

    const val DB_NAME = "budget_gamer_database.db"
}