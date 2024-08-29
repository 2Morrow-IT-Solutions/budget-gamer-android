package com.tomorrowit.budgetgamer.data.repository_impl

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.tomorrowit.budgetgamer.common.config.Constants.FirebaseDatabase.ACCEPTED_LINKS
import com.tomorrowit.budgetgamer.common.config.Constants.FirebaseDatabase.ARTICLE_LINKS
import com.tomorrowit.budgetgamer.common.config.Constants.FirebaseDatabase.DENIED_LINKS
import com.tomorrowit.budgetgamer.common.config.Constants.FirebaseDatabase.GAME_LINKS
import com.tomorrowit.budgetgamer.common.config.extensions.tag
import com.tomorrowit.budgetgamer.common.di.IoDispatcher
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.ExistsRepo
import com.tomorrowit.budgetgamer.domain.repo.ExistsResponse
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class ExistsRepoImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val authRepo: AuthRepo,
    private val loggerRepo: LoggerRepo,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : ExistsRepo {

    override fun userHasBan(): Flow<ExistsResponse> {
        val currentUser = authRepo.getCurrentUserUid()
        if (currentUser != null) {
            val query = firebaseDatabase.getReference("users/${currentUser}/read_only/banned")
            return documentExists(query)
        } else {
            return flow { emit(ExistsResponse.IsFailure(Exception("User not logged in"))) }
        }
    }

    override suspend fun isGameAcceptedLink(itemUrl: String): ExistsResponse =
        withContext(coroutineDispatcher) {
            val currentUser = authRepo.getCurrentUserUid()
            return@withContext if (currentUser != null) {
                val query = firebaseDatabase.getReference("$GAME_LINKS/$ACCEPTED_LINKS")
                itemUrlExists(query, itemUrl)
            } else {
                ExistsResponse.IsFailure(Exception("User ID is null. Unable to determine URL status."))
            }
        }

    override suspend fun isArticleAcceptedLink(itemUrl: String): ExistsResponse =
        withContext(coroutineDispatcher) {
            val currentUser = authRepo.getCurrentUserUid()
            return@withContext if (currentUser != null) {
                val query = firebaseDatabase.getReference("$ARTICLE_LINKS/$ACCEPTED_LINKS")
                itemUrlExists(query, itemUrl)
            } else {
                ExistsResponse.IsFailure(Exception("User ID is null. Unable to determine URL status."))
            }
        }

    override suspend fun isGameDeclinedLink(itemUrl: String): ExistsResponse =
        withContext(coroutineDispatcher) {
            val currentUser = authRepo.getCurrentUserUid()
            return@withContext if (currentUser != null) {
                val query = firebaseDatabase.getReference("$GAME_LINKS/$DENIED_LINKS")
                itemUrlExists(query, itemUrl)
            } else {
                ExistsResponse.IsFailure(Exception("User ID is null. Unable to determine URL status."))
            }
        }

    override suspend fun isArticleDeclinedLink(itemUrl: String): ExistsResponse =
        withContext(coroutineDispatcher) {
            val currentUser = authRepo.getCurrentUserUid()
            return@withContext if (currentUser != null) {
                val query = firebaseDatabase.getReference("$ARTICLE_LINKS/$DENIED_LINKS")
                itemUrlExists(query, itemUrl)
            } else {
                ExistsResponse.IsFailure(Exception("User ID is null. Unable to determine URL status."))
            }
        }

    private fun documentExists(query: Query): Flow<ExistsResponse> {
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value: Boolean? = snapshot.getValue(Boolean::class.java)
                    if (value != null) {
                        trySend(ExistsResponse.IsSuccess(value))
                        loggerRepo.info(tag(), "Success: document exists")
                    } else {
                        trySend(ExistsResponse.IsFailure(Exception("The parameter is null")))
                        loggerRepo.info(tag(), "The parameter is null")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(ExistsResponse.IsFailure(error.toException()))
                    loggerRepo.info(tag(), error.toString())
                }

            }.also {
                query.addValueEventListener(it)
            }

            awaitClose { query.removeEventListener(listener) }
        }.catch {
            emit(ExistsResponse.IsFailure(Exception(it.message)))
        }.flowOn(coroutineDispatcher)
    }

    private suspend fun itemUrlExists(query: Query, itemUrl: String): ExistsResponse {
        return suspendCancellableCoroutine { continuation ->
            var isResumed = false

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!isResumed) {
                        val urlExists = snapshot.children.any { it.value == itemUrl }

                        if (urlExists) {
                            loggerRepo.info(tag(), "URL already denied.")
                            continuation.resume(ExistsResponse.IsSuccess(true))
                        } else {
                            loggerRepo.info(
                                tag(),
                                "URL was not denied, check to see if it was accepted."
                            )
                            continuation.resume(ExistsResponse.IsSuccess(false))
                        }
                        isResumed = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (!isResumed) {
                        loggerRepo.info(tag(), error.toString())
                        continuation.resumeWithException(error.toException())
                        isResumed = true
                    }
                }
            }

            // Register the listener
            query.addValueEventListener(listener)

            // Handle coroutine cancellation
            continuation.invokeOnCancellation {
                query.removeEventListener(listener)
            }
        }
    }
}