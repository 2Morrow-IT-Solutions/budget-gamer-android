package com.tomorrowit.budgetgamer.domain.repo.room_repo

import com.tomorrowit.budgetgamer.data.entities.ClientPlatformEntity
import kotlinx.coroutines.flow.Flow

interface ClientPlatformRepo {

    suspend fun upsert(clientPlatformEntity: ClientPlatformEntity)

    suspend fun deleteById(id: String)

    fun getClientPlatformById(id: String): Flow<ClientPlatformEntity?>
}