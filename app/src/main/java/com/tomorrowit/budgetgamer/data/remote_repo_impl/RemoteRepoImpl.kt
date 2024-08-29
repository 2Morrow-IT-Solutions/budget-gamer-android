package com.tomorrowit.budgetgamer.data.remote_repo_impl

import com.google.gson.JsonObject
import com.tomorrowit.budgetgamer.common.di.IoDispatcher
import com.tomorrowit.budgetgamer.data.retrofit.RestClient
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.remote_repo.CallResult
import com.tomorrowit.budgetgamer.domain.remote_repo.RemoteRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RemoteRepoImpl @Inject constructor(
    private val authRepo: AuthRepo,
    private val restClient: RestClient,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : RemoteRepo {

    override suspend fun addArticleLinkUrl(link: String): CallResult {
        val currentUser =
            authRepo.getCurrentUserUid() ?: return CallResult.IsFailure("User not logged in")
        val call = restClient.articleLinkHandler(link, currentUser)
        return executeCall(coroutineDispatcher, call)
    }

    override suspend fun addGameLinkUrl(link: String): CallResult {
        val currentUser =
            authRepo.getCurrentUserUid() ?: return CallResult.IsFailure("User not logged in")
        val call = restClient.gameLinkHandler(link, currentUser)
        return executeCall(coroutineDispatcher, call)
    }

    private suspend fun executeCall(
        coroutineDispatcher: CoroutineDispatcher,
        call: Call<JsonObject>
    ): CallResult {
        return withContext(coroutineDispatcher) {
            suspendCancellableCoroutine { continuation ->
                call.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if (response.isSuccessful) {
                            val isSuccess: Boolean? = response.body()?.get("success")?.asBoolean
                            if (isSuccess != null) {
                                continuation.resume(CallResult.IsSuccess)
                            } else {
                                continuation.resume(CallResult.IsFailure("API response is bad."))
                            }
                        } else {
                            continuation.resume(CallResult.IsFailure("Response not successful: ${response.message()}"))
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })

                continuation.invokeOnCancellation {
                    call.cancel()
                }
            }
        }
    }
}