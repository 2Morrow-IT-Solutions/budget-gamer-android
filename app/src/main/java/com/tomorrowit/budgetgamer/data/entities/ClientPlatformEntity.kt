package com.tomorrowit.budgetgamer.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_platform_table")
data class ClientPlatformEntity(
    @PrimaryKey
    val id: String,
    val maintenance: Boolean = false,
    val minVersion: String = "0.0.0",
)