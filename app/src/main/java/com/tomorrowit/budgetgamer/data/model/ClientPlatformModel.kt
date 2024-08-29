package com.tomorrowit.budgetgamer.data.model

data class ClientPlatformModel(
    val maintenance: Boolean = false,
    val min_version: String = "0.0.0",
    override var key: String = "",
) : FirebaseModel
