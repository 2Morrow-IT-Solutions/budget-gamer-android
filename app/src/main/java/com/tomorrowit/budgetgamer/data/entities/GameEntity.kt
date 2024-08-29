package com.tomorrowit.budgetgamer.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "game_table",
    indices = [
        Index(value = ["providerId"]),
        Index(value = ["subscriptionId"])
    ]
)
data class GameEntity(
    @PrimaryKey
    val gameId: String,
    var cover: String = "",
    var coverPortrait: String = "",
    var description: String = "",
    var developer: String = "",
    var endDate: Long = 0,
    var free: Boolean = false,
    var giveaway: Boolean = false,
    var name: String = "",
    var platformId: String = "",
    var providerId: String? = null,
    var providerUrl: String = "",
    var publisher: String = "",
    var releaseDate: String = "",
    var startDate: Long = 0,
    var subscriptionId: String = ""
)