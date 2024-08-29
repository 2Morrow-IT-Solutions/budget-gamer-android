package com.tomorrowit.budgetgamer.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tomorrowit.budgetgamer.data.entities.SubscriptionEntity
import com.tomorrowit.budgetgamer.data.entities.SubscriptionWithGames
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Transaction
    suspend fun upsert(subscriptionEntity: SubscriptionEntity) {
        val insertResult = insert(subscriptionEntity)
        if (insertResult == -1L) {
            update(subscriptionEntity)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(subscriptionEntity: SubscriptionEntity): Long

    @Update
    suspend fun update(subscriptionEntity: SubscriptionEntity)

    @Query("DELETE FROM article_table WHERE id = :id")
    suspend fun deleteById(id: String)

    @Transaction
    @Query(
        """
        SELECT * FROM subscription_table
        WHERE id IN (
            SELECT DISTINCT subscriptionId FROM game_table
            WHERE endDate > :actualTime AND free = 0
        )
    """
    )
    fun getSubscriptionsWithGames(actualTime: Long): Flow<List<SubscriptionWithGames>>
}