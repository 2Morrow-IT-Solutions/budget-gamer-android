package com.tomorrowit.budgetgamer.data.repository_impl.room_repo_impl

import com.tomorrowit.budgetgamer.common.di.IoDispatcher
import com.tomorrowit.budgetgamer.data.entities.ClientPlatformEntity
import com.tomorrowit.budgetgamer.data.local.room.dao.ClientPlatformDao
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ClientPlatformRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClientPlatformRepoImpl @Inject constructor(
    private val clientPlatformDao: ClientPlatformDao,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : ClientPlatformRepo {

    override suspend fun upsert(clientPlatformEntity: ClientPlatformEntity) {
        withContext(coroutineDispatcher) {
            clientPlatformDao.upsert(clientPlatformEntity)
        }
    }

    override suspend fun deleteById(id: String) {
        withContext(coroutineDispatcher) {
            clientPlatformDao.deleteById(id)
        }
    }

    override fun getClientPlatformById(id: String): Flow<ClientPlatformEntity?> {
        return clientPlatformDao.getClientPlatformById(id).flowOn(coroutineDispatcher)
    }
}