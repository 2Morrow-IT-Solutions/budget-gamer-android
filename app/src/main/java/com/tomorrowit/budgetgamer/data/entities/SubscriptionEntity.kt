package com.tomorrowit.budgetgamer.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription_table")
data class SubscriptionEntity(
    @PrimaryKey
    var id: String,
    var colour: String = "",
    var logo: String = "",
    var name: String = ""
)