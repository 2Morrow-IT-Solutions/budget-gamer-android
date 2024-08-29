package com.tomorrowit.budgetgamer.domain.usecases

import com.tomorrowit.budgetgamer.domain.repo.room_repo.ArticleRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.GameRepo

class ClearOldDataUseCase(
    private val gameRepo: GameRepo,
    private val articleRepo: ArticleRepo
) {
    private val oneDayInMillis: Long by lazy {
        24 * 60 * 60 * 1000
    }
    private val twoWeeksInMillis: Long by lazy {
        oneDayInMillis * 14
    }

    suspend operator fun invoke() {
        val specificTimestamp = System.currentTimeMillis() - twoWeeksInMillis
        gameRepo.deleteGameAfter(specificTimestamp)
        articleRepo.deleteArticlesAfter(specificTimestamp)
    }
}