package com.tomorrowit.budgetgamer.domain.usecases.activity

import android.app.Activity
import android.content.Intent

class ShareGameUseCase(private val activity: Activity) {

    //Todo: Resource here please.
    fun openGameLink(gameName: String, gameLink: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey! I found this free game on the BudgetGamer app: $gameName. Check it out at this link: $gameLink"
        )
        sendIntent.type = "text/plain"
        val shareIntent = Intent.createChooser(sendIntent, null)
        activity.startActivity(shareIntent)
    }
}