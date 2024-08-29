package com.tomorrowit.budgetgamer.domain.repo

import kotlinx.coroutines.flow.Flow

interface ExistsRepo {

    fun userHasBan(): Flow<ExistsResponse>

    suspend fun isGameAcceptedLink(itemUrl: String): ExistsResponse

    suspend fun isArticleAcceptedLink(itemUrl: String): ExistsResponse

    suspend fun isGameDeclinedLink(itemUrl: String): ExistsResponse

    suspend fun isArticleDeclinedLink(itemUrl: String): ExistsResponse

}

sealed class ExistsResponse {
    data class IsSuccess(val exists: Boolean) : ExistsResponse()
    data class IsFailure(val exception: Exception) : ExistsResponse()
}