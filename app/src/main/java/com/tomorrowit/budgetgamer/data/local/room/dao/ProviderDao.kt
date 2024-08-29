package com.tomorrowit.budgetgamer.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tomorrowit.budgetgamer.data.entities.ProviderEntity

@Dao
interface ProviderDao {
    @Transaction
    suspend fun upsert(providerEntity: ProviderEntity) {
        val insertResult = insert(providerEntity)
        if (insertResult == -1L) {
            update(providerEntity)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(providerEntity: ProviderEntity): Long

    @Update
    suspend fun update(providerEntity: ProviderEntity)

    @Query("DELETE FROM article_table WHERE id = :id")
    suspend fun deleteById(id: String)
}