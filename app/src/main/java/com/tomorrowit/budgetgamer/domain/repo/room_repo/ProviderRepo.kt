package com.tomorrowit.budgetgamer.domain.repo.room_repo

import com.tomorrowit.budgetgamer.data.entities.ProviderEntity

interface ProviderRepo {

    suspend fun upsert(providerEntity: ProviderEntity)

    suspend fun deleteById(id: String)
}