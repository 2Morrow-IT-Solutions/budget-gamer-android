package com.tomorrowit.budgetgamer.data.repository_impl.room_repo_impl

import com.tomorrowit.budgetgamer.common.di.IoDispatcher
import com.tomorrowit.budgetgamer.data.entities.GameEntity
import com.tomorrowit.budgetgamer.data.entities.GamePlatformCrossRef
import com.tomorrowit.budgetgamer.data.entities.GameWithProvider
import com.tomorrowit.budgetgamer.data.local.room.dao.GameDao
import com.tomorrowit.budgetgamer.domain.repo.room_repo.GameRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepoImpl @Inject constructor(
    private val gameDao: GameDao,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : GameRepo {

    override suspend fun upsert(gameEntity: GameEntity) {
        withContext(coroutineDispatcher) {
            gameDao.upsert(gameEntity)
        }
    }

    override suspend fun deleteById(id: String) {
        withContext(coroutineDispatcher) {
            gameDao.deleteById(id)
        }
    }

    override fun getAllGames(): Flow<List<GameEntity>> {
        return gameDao.getAllGames().flowOn(coroutineDispatcher)
    }

    override fun getGameById(gameId: String): Flow<GameWithProvider?> {
        return gameDao.getGameById(gameId).flowOn(coroutineDispatcher)
    }

    override fun getAllActiveGames(actualTime: Long): Flow<List<GameWithProvider>> {
        return gameDao.getAllActiveGames(actualTime).flowOn(coroutineDispatcher)
    }

    override suspend fun insertGamePlatformCrossRefs(crossRefs: List<GamePlatformCrossRef>) {
        withContext(coroutineDispatcher) {
            gameDao.insertGamePlatformCrossRefs(crossRefs)
        }
    }

    override suspend fun upsertGameWithPlatforms(gameEntity: GameEntity) {
        withContext(coroutineDispatcher) {
            gameDao.upsertGameWithPlatforms(gameEntity)
        }
    }

    override suspend fun deleteGameAfter(specificTimestamp: Long) {
        withContext(coroutineDispatcher) {
            gameDao.deleteGamesAfter(specificTimestamp)
        }
    }
}