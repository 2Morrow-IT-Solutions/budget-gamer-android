package com.tomorrowit.budgetgamer.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tomorrowit.budgetgamer.data.entities.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Transaction
    suspend fun upsert(articleEntity: ArticleEntity) {
        val insertResult = insert(articleEntity)
        if (insertResult == -1L) {
            update(articleEntity)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(articleEntity: ArticleEntity): Long

    @Update
    suspend fun update(articleEntity: ArticleEntity)

    @Query("DELETE FROM article_table WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM article_table WHERE endDate > :currentTime ORDER BY endDate DESC")
    fun getAllArticles(currentTime: Long): Flow<List<ArticleEntity>>

    @Query("DELETE FROM article_table WHERE endDate < :specificTimestamp")
    suspend fun deleteArticlesAfter(specificTimestamp: Long)

}