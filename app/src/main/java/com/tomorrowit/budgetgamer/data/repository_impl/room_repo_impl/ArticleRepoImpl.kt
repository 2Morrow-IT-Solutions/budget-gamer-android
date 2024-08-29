package com.tomorrowit.budgetgamer.data.repository_impl.room_repo_impl

import com.tomorrowit.budgetgamer.common.di.IoDispatcher
import com.tomorrowit.budgetgamer.data.entities.ArticleEntity
import com.tomorrowit.budgetgamer.data.local.room.dao.ArticleDao
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ArticleRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArticleRepoImpl @Inject constructor(
    private val articleDao: ArticleDao,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : ArticleRepo {

    override suspend fun upsert(articleEntity: ArticleEntity) {
        withContext(coroutineDispatcher) {
            articleDao.upsert(articleEntity)
        }
    }

    override suspend fun deleteById(id: String) {
        withContext(coroutineDispatcher) {
            articleDao.deleteById(id)
        }
    }

    override fun getAllArticles(actualTime: Long): Flow<List<ArticleEntity>> {
        return articleDao.getAllArticles(actualTime).flowOn(coroutineDispatcher)
    }

    override suspend fun deleteArticlesAfter(specificTimestamp: Long) {
        withContext(coroutineDispatcher) {

            articleDao.deleteArticlesAfter(specificTimestamp)
        }
    }
}