package com.tomorrowit.budgetgamer.domain.usecases.sync

import com.tomorrowit.budgetgamer.data.mapper.ArticleMapper
import com.tomorrowit.budgetgamer.domain.repo.CollectionRepo
import com.tomorrowit.budgetgamer.domain.repo.CollectionResult
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ArticleRepo

class SyncArticlesUseCase(
    private val collectionRepo: CollectionRepo,
    private val articleMapper: ArticleMapper,
    private val articleRepo: ArticleRepo
) {
    suspend operator fun invoke() {
        collectionRepo.getAllArticles().collect { response ->
            if (response is CollectionResult.IsSuccess) {
                response.data.forEach {
                    articleRepo.upsert(articleMapper.mapFromFirebaseModel(it))
                }
            }
        }
    }
}