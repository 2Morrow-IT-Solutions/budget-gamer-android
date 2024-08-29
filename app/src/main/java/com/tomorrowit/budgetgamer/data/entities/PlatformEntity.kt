package com.tomorrowit.budgetgamer.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "platform_table",
    indices = [
        Index(value = ["platformId"])
    ]
)
data class PlatformEntity(
    @PrimaryKey
    var platformId: String,
    var colourBackground: String = "",
    var colourMargin: String = "",
    var colourText: String = "",
    var logo: String = "",
    var name: String = "",
    var priority: Int = 0
)
