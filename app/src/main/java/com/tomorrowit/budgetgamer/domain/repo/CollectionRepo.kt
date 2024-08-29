package com.tomorrowit.budgetgamer.domain.repo

import com.tomorrowit.budgetgamer.data.model.ArticleModel
import com.tomorrowit.budgetgamer.data.model.GameModel
import kotlinx.coroutines.flow.Flow

interface CollectionRepo {
    fun getAllArticles(): Flow<CollectionResult<List<ArticleModel>>>

    fun getAllFreeGames(): Flow<CollectionResult<List<GameModel>>>

    fun getAllSubscriptionGames(): Flow<CollectionResult<List<GameModel>>>
}

sealed class CollectionResult<out T> {
    data class IsSuccess<out T>(val data: List<T>) : CollectionResult<List<T>>()
    data class IsFailure(val exception: Exception) : CollectionResult<Nothing>()
}