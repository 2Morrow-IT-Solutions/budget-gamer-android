package com.tomorrowit.budgetgamer.data.model

data class ArticleModel(
    override var key: String = "",
    val cover: String = "",
    val description: String = "",
    val domain: String = "",
    val end_date: Long = 0,
    val link: String = "",
    val start_date: Long = 0,
    val title: String = ""
) : FirebaseModel
