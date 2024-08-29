package com.tomorrowit.budgetgamer.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tomorrowit.budgetgamer.data.entities.ClientPlatformEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientPlatformDao {
    @Transaction
    suspend fun upsert(clientPlatformEntity: ClientPlatformEntity) {
        val insertResult = insert(clientPlatformEntity)
        if (insertResult == -1L) {
            update(clientPlatformEntity)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(clientPlatformEntity: ClientPlatformEntity): Long

    @Update
    suspend fun update(clientPlatformEntity: ClientPlatformEntity)

    @Query("DELETE FROM client_platform_table WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM client_platform_table WHERE id = :id")
    fun getClientPlatformById(id: String): Flow<ClientPlatformEntity?>
}