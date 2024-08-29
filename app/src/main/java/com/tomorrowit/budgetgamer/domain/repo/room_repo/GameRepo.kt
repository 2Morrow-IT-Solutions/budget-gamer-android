package com.tomorrowit.budgetgamer.domain.repo.room_repo

import com.tomorrowit.budgetgamer.data.entities.GameEntity
import com.tomorrowit.budgetgamer.data.entities.GamePlatformCrossRef
import com.tomorrowit.budgetgamer.data.entities.GameWithProvider
import kotlinx.coroutines.flow.Flow

interface GameRepo {

    suspend fun upsert(gameEntity: GameEntity)

    suspend fun deleteById(id: String)

    fun getAllGames(): Flow<List<GameEntity>>

    fun getGameById(gameId: String): Flow<GameWithProvider?>

    fun getAllActiveGames(actualTime: Long): Flow<List<GameWithProvider>>

    suspend fun insertGamePlatformCrossRefs(crossRefs: List<GamePlatformCrossRef>)

    suspend fun upsertGameWithPlatforms(gameEntity: GameEntity)

    suspend fun deleteGameAfter(specificTimestamp: Long)
}