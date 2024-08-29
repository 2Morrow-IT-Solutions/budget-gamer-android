package com.tomorrowit.budgetgamer.data.repository_impl.room_repo_impl

import com.tomorrowit.budgetgamer.common.di.IoDispatcher
import com.tomorrowit.budgetgamer.data.entities.PlatformEntity
import com.tomorrowit.budgetgamer.data.local.room.dao.PlatformDao
import com.tomorrowit.budgetgamer.domain.repo.room_repo.PlatformRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlatformRepoImpl @Inject constructor(
    private val platformDao: PlatformDao,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : PlatformRepo {

    override suspend fun upsert(platformEntity: PlatformEntity) {
        withContext(coroutineDispatcher) {
            platformDao.upsert(platformEntity)
        }
    }

    override suspend fun deleteById(id: String) {
        withContext(coroutineDispatcher) {
            platformDao.deleteById(id)
        }
    }

    override fun getPlatformById(id: String): Flow<PlatformEntity?> {
        return platformDao.getPlatformById(id).flowOn(coroutineDispatcher)
    }

    override fun getPlatformsByIds(ids: List<String>): Flow<List<PlatformEntity>> {
        return platformDao.getPlatformsByIds(ids).flowOn(coroutineDispatcher)
    }

}