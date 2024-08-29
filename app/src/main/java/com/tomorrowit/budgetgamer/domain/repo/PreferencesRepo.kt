package com.tomorrowit.budgetgamer.domain.repo

import kotlinx.coroutines.flow.Flow

interface PreferencesRepo {
    fun getMessagingToken(): Flow<String>

    suspend fun setMessagingToken(token: String)

    suspend fun clearAll()
}