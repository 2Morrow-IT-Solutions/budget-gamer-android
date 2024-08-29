package com.tomorrowit.budgetgamer.domain.repo.room_repo

import com.tomorrowit.budgetgamer.data.entities.PlatformEntity
import kotlinx.coroutines.flow.Flow

interface PlatformRepo {

    suspend fun upsert(platformEntity: PlatformEntity)

    suspend fun deleteById(id: String)

    fun getPlatformById(id: String): Flow<PlatformEntity?>

    fun getPlatformsByIds(ids: List<String>): Flow<List<PlatformEntity>>
}