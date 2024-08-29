package com.tomorrowit.budgetgamer.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tomorrowit.budgetgamer.data.entities.GameEntity
import com.tomorrowit.budgetgamer.data.entities.GamePlatformCrossRef
import com.tomorrowit.budgetgamer.data.entities.GameWithProvider
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Transaction
    suspend fun upsert(gameEntity: GameEntity) {
        val insertResult = insert(gameEntity)
        if (insertResult == -1L) {
            update(gameEntity)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(gameEntity: GameEntity): Long

    @Update
    suspend fun update(gameEntity: GameEntity)

    @Query("DELETE FROM game_table WHERE gameId = :gameId")
    suspend fun deleteById(gameId: String)

    @Query("SELECT * FROM game_table ORDER BY endDate DESC")
    fun getAllGames(): Flow<List<GameEntity>>

    @Transaction
    @Query("SELECT * FROM game_table WHERE gameId = :gameId LIMIT 1")
    fun getGameById(gameId: String): Flow<GameWithProvider?>

    @Transaction
    @Query("SELECT * FROM game_table WHERE endDate > :actualTime AND free = 1 ORDER BY endDate DESC")
    fun getAllActiveGames(actualTime: Long): Flow<List<GameWithProvider>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGamePlatformCrossRefs(crossRefs: List<GamePlatformCrossRef>)

    @Transaction
    suspend fun upsertGameWithPlatforms(
        gameEntity: GameEntity
    ) {
        val gameId = insert(gameEntity)
        if (gameId == -1L) {
            update(gameEntity)
        }
        val platformIds = gameEntity.platformId.split(",").map { it.trim() }
        val crossRefs = platformIds.map { platformId ->
            GamePlatformCrossRef(platformId, gameEntity.gameId)
        }
        insertGamePlatformCrossRefs(crossRefs)
    }

    @Query("DELETE FROM game_table WHERE endDate < :specificTimestamp")
    suspend fun deleteGamesAfter(specificTimestamp: Long)
}