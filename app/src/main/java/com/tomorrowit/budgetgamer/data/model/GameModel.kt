package com.tomorrowit.budgetgamer.data.model

data class GameModel(
    override var key: String = "",
    val cover: String = "",
    val cover_portrait: String = "",
    val description: String = "",
    val developer: String = "",
    val end_date: Long = 0,
    val free: Boolean = false,
    val giveaway: Boolean = false,
    val name: String = "",
    val platform_ids: String = "",
    val provider_id: String = "",
    val provider_url: String = "",
    val publisher: String = "",
    val release_date: String = "",
    val start_date: Long = 0,
    var subscription_id: String = ""
) : FirebaseModel
//    var gamePlatforms: List<PlatformModel> = listOf(),
//    var gameProvider: ProviderModel? = null,
//    var gameSubscription: SubscriptionModel? = null,