package com.tomorrowit.budgetgamer.domain.usecases.sync

import com.tomorrowit.budgetgamer.data.mapper.GameMapper
import com.tomorrowit.budgetgamer.domain.repo.CollectionRepo
import com.tomorrowit.budgetgamer.domain.repo.CollectionResult
import com.tomorrowit.budgetgamer.domain.repo.room_repo.GameRepo

class SyncSubscriptionGamesUseCase(
    private val collectionRepo: CollectionRepo,
    private val gameMapper: GameMapper,
    private val gameRepo: GameRepo
) {
    suspend operator fun invoke() {
        collectionRepo.getAllSubscriptionGames().collect { response ->
            if (response is CollectionResult.IsSuccess) {
                response.data.forEach {
                    gameRepo.upsert(gameMapper.mapFromFirebaseModel(it))
                    gameRepo.upsertGameWithPlatforms(gameMapper.mapFromFirebaseModel(it))
                }
            }
        }
    }
}