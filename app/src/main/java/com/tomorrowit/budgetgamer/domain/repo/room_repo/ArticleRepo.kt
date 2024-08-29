package com.tomorrowit.budgetgamer.domain.repo.room_repo

import com.tomorrowit.budgetgamer.data.entities.ArticleEntity
import kotlinx.coroutines.flow.Flow

interface ArticleRepo {

    suspend fun upsert(articleEntity: ArticleEntity)

    suspend fun deleteById(id: String)

    fun getAllArticles(actualTime: Long): Flow<List<ArticleEntity>>

    suspend fun deleteArticlesAfter(specificTimestamp: Long)
}