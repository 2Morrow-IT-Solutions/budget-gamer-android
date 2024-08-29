package com.tomorrowit.budgetgamer.data.model

data class PlatformSettingsModel(
    override var key: String = "platform_settings",
    val android: ClientPlatformModel = ClientPlatformModel(key = "android"),
    val ios: ClientPlatformModel = ClientPlatformModel(key = "ios"),
    val platforms: HashMap<String, PlatformModel> = HashMap(),
    val providers: HashMap<String, ProviderModel> = HashMap(),
    val subscriptions: HashMap<String, SubscriptionModel> = HashMap(),
    val web: ClientPlatformModel = ClientPlatformModel(key = "web")
) : FirebaseModel