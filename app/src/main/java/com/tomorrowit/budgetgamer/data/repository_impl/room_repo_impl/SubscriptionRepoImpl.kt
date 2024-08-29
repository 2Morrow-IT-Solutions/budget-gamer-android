package com.tomorrowit.budgetgamer.data.repository_impl.room_repo_impl

import com.tomorrowit.budgetgamer.common.di.IoDispatcher
import com.tomorrowit.budgetgamer.data.entities.SubscriptionEntity
import com.tomorrowit.budgetgamer.data.entities.SubscriptionWithGames
import com.tomorrowit.budgetgamer.data.local.room.dao.SubscriptionDao
import com.tomorrowit.budgetgamer.domain.repo.room_repo.SubscriptionRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubscriptionRepoImpl @Inject constructor(
    private val subscriptionDao: SubscriptionDao,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : SubscriptionRepo {

    override suspend fun upsert(subscriptionEntity: SubscriptionEntity) {
        withContext(coroutineDispatcher) {
            subscriptionDao.upsert(subscriptionEntity)
        }
    }

    override suspend fun deleteById(id: String) {
        withContext(coroutineDispatcher) {
            subscriptionDao.deleteById(id)
        }
    }

    override fun getSubscriptionsWithGames(actualTime: Long): Flow<List<SubscriptionWithGames>> {
        return subscriptionDao.getSubscriptionsWithGames(actualTime).flowOn(coroutineDispatcher)
    }
}