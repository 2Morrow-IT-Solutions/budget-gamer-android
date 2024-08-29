package com.tomorrowit.budgetgamer.domain.usecases

import com.tomorrowit.budgetgamer.domain.repo.ExistsRepo
import com.tomorrowit.budgetgamer.domain.repo.ExistsResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class IsNewUrlUseCase(
    private val existsRepo: ExistsRepo,
    private val coroutineDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(isGameUrl: Boolean, url: String): UrlCheckResult {
        return if (isGameUrl) {
            verifyGameUrl(url)
        } else {
            verifyArticleUrl(url)
        }
    }

    private suspend fun verifyGameUrl(url: String): UrlCheckResult {
        return withContext(coroutineDispatcher) {
            val gameAcceptedJob = async { existsRepo.isGameAcceptedLink(url) }
            val gameDeclinedJob = async { existsRepo.isGameDeclinedLink(url) }

            val gameAcceptedResult = gameAcceptedJob.await()
            val gameDeclinedResult = gameDeclinedJob.await()

            if (gameAcceptedResult is ExistsResponse.IsSuccess && gameDeclinedResult is ExistsResponse.IsSuccess) {
                if (!gameAcceptedResult.exists && !gameDeclinedResult.exists) {
                    return@withContext UrlCheckResult.IsNew
                } else {
                    if (gameAcceptedResult.exists) {
                        return@withContext UrlCheckResult.IsAccepted
                    } else {
                        return@withContext UrlCheckResult.IsDenied
                    }
                }
            } else {
                if (gameAcceptedResult is ExistsResponse.IsFailure) {
                    return@withContext UrlCheckResult.Error(gameAcceptedResult.exception)
                } else if (gameDeclinedResult is ExistsResponse.IsFailure) {
                    return@withContext UrlCheckResult.Error(gameDeclinedResult.exception)
                } else {
                    return@withContext UrlCheckResult.Error(Exception("Unknown error"))
                }
            }
        }
    }

    private suspend fun verifyArticleUrl(url: String): UrlCheckResult {
        return withContext(coroutineDispatcher) {
            val articleAcceptedJob = async { existsRepo.isArticleAcceptedLink(url) }
            val articleDeclinedJob = async { existsRepo.isArticleDeclinedLink(url) }

            val articleAcceptedResult = articleAcceptedJob.await()
            val articleDeclinedResult = articleDeclinedJob.await()

            if (articleAcceptedResult is ExistsResponse.IsSuccess && articleDeclinedResult is ExistsResponse.IsSuccess) {
                if (!articleAcceptedResult.exists && !articleDeclinedResult.exists) {
                    return@withContext UrlCheckResult.IsNew
                } else {
                    if (articleAcceptedResult.exists) {
                        return@withContext UrlCheckResult.IsAccepted
                    } else {
                        return@withContext UrlCheckResult.IsDenied
                    }
                }
            } else {
                if (articleAcceptedResult is ExistsResponse.IsFailure) {
                    return@withContext UrlCheckResult.Error(articleAcceptedResult.exception)
                } else if (articleDeclinedResult is ExistsResponse.IsFailure) {
                    return@withContext UrlCheckResult.Error(articleDeclinedResult.exception)
                } else {
                    return@withContext UrlCheckResult.Error(Exception("Unknown error"))
                }
            }
        }
    }
}

sealed class UrlCheckResult {
    data object IsAccepted : UrlCheckResult()
    data object IsDenied : UrlCheckResult()
    data object IsNew : UrlCheckResult()
    data class Error(val message: Exception) : UrlCheckResult()
}