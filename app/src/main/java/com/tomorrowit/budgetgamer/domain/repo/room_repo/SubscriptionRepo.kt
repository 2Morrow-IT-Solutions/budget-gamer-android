package com.tomorrowit.budgetgamer.domain.repo.room_repo

import com.tomorrowit.budgetgamer.data.entities.SubscriptionEntity
import com.tomorrowit.budgetgamer.data.entities.SubscriptionWithGames
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepo {

    suspend fun upsert(subscriptionEntity: SubscriptionEntity)

    suspend fun deleteById(id: String)

    fun getSubscriptionsWithGames(actualTime: Long): Flow<List<SubscriptionWithGames>>
}