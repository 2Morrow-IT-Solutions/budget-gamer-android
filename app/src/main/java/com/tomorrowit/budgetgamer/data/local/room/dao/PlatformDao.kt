package com.tomorrowit.budgetgamer.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tomorrowit.budgetgamer.data.entities.PlatformEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlatformDao {
    @Transaction
    suspend fun upsert(platformEntity: PlatformEntity) {
        val insertResult = insert(platformEntity)
        if (insertResult == -1L) {
            update(platformEntity)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(platformEntity: PlatformEntity): Long

    @Update
    suspend fun update(platformEntity: PlatformEntity)

    @Query("DELETE FROM platform_table WHERE platformId = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM platform_table WHERE platformId = :id LIMIT 1")
    fun getPlatformById(id: String): Flow<PlatformEntity?>

    @Query("SELECT * FROM platform_table WHERE platformId IN (:ids)")
    fun getPlatformsByIds(ids: List<String>): Flow<List<PlatformEntity>>
}