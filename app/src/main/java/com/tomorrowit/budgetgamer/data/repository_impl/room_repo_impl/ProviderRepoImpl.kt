package com.tomorrowit.budgetgamer.data.repository_impl.room_repo_impl

import com.tomorrowit.budgetgamer.common.di.IoDispatcher
import com.tomorrowit.budgetgamer.data.entities.ProviderEntity
import com.tomorrowit.budgetgamer.data.local.room.dao.ProviderDao
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ProviderRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProviderRepoImpl @Inject constructor(
    private val providerDao: ProviderDao,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : ProviderRepo {

    override suspend fun upsert(providerEntity: ProviderEntity) {
        withContext(coroutineDispatcher) {
            providerDao.upsert(providerEntity)
        }
    }

    override suspend fun deleteById(id: String) {
        withContext(coroutineDispatcher) {
            providerDao.deleteById(id)
        }
    }
}