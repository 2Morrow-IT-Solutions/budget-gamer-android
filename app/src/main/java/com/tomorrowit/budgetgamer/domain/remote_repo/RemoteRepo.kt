package com.tomorrowit.budgetgamer.domain.remote_repo

interface RemoteRepo {

    suspend fun addArticleLinkUrl(link: String): CallResult

    suspend fun addGameLinkUrl(link: String): CallResult
}

sealed class CallResult {
    data object IsSuccess : CallResult()
    data class IsFailure(val error: String) : CallResult()
}