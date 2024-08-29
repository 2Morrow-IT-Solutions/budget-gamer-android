package com.tomorrowit.budgetgamer.domain.usecases.sync

import com.tomorrowit.budgetgamer.data.mapper.GameMapper
import com.tomorrowit.budgetgamer.domain.repo.CollectionRepo
import com.tomorrowit.budgetgamer.domain.repo.CollectionResult
import com.tomorrowit.budgetgamer.domain.repo.room_repo.GameRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SyncFreeGamesUseCase(
    private val collectionRepo: CollectionRepo,
    private val gameMapper: GameMapper,
    private val gameRepo: GameRepo

) {
    private var _firstSyncDone = MutableStateFlow<Boolean>(false)
    val firstSyncDone: StateFlow<Boolean> get() = _firstSyncDone

    suspend operator fun invoke() {
        collectionRepo.getAllFreeGames().collect { response ->
            if (!_firstSyncDone.value) {
                _firstSyncDone.value = true
            }
            if (response is CollectionResult.IsSuccess) {
                response.data.forEach {
                    gameRepo.upsert(gameMapper.mapFromFirebaseModel(it))
                }
            }
        }
    }
}