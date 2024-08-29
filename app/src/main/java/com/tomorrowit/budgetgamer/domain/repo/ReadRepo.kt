package com.tomorrowit.budgetgamer.domain.repo

import com.tomorrowit.budgetgamer.data.model.PlatformSettingsModel
import kotlinx.coroutines.flow.Flow

interface ReadRepo {

    fun getPlatformSettings(): Flow<ReadResponse<PlatformSettingsModel>>

    fun getIsMyUserBanned(): Flow<ReadResponse<Boolean>>
}

sealed class ReadResponse<out T> {
    data class IsSuccess<out T>(val data: T) : ReadResponse<T>()
    data class IsFailure(val exception: Exception) : ReadResponse<Nothing>()
}