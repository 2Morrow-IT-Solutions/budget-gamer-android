package com.tomorrowit.budgetgamer.data.model

data class SubscriptionGameModel(
    override var key: String,
    val subscriptionId: String = "",
    val game: GameModel
) : FirebaseModel