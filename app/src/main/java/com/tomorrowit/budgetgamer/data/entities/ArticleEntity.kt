package com.tomorrowit.budgetgamer.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_table")
data class ArticleEntity(
    @PrimaryKey
    var id: String,
    var cover: String = "",
    var description: String = "",
    var domain: String = "",
    var endDate: Long = 0,
    var link: String = "",
    var startDate: Long = 0,
    var title: String = ""
)