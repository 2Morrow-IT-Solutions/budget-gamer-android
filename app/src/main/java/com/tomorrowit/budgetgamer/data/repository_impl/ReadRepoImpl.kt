package com.tomorrowit.budgetgamer.data.repository_impl

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tomorrowit.budgetgamer.common.config.extensions.tag
import com.tomorrowit.budgetgamer.common.di.IoDispatcher
import com.tomorrowit.budgetgamer.data.model.FirebaseModel
import com.tomorrowit.budgetgamer.data.model.PlatformSettingsModel
import com.tomorrowit.budgetgamer.domain.repo.AuthRepo
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import com.tomorrowit.budgetgamer.domain.repo.ReadRepo
import com.tomorrowit.budgetgamer.domain.repo.ReadResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class ReadRepoImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val authRepo: AuthRepo,
    private val loggerRepo: LoggerRepo,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : ReadRepo {
    override fun getPlatformSettings(): Flow<ReadResponse<PlatformSettingsModel>> {
        val query = firebaseDatabase.getReference("platform_settings")
        return getFirestoreDocument(query)
    }

    override fun getIsMyUserBanned(): Flow<ReadResponse<Boolean>> = flow {
        val currentUser = authRepo.getCurrentUserUid()

        if (currentUser != null) {
            try {
                val isBanned = suspendCancellableCoroutine<Boolean> { continuation ->
                    firebaseDatabase.getReference("users")
                        .child(currentUser)
                        .child("read_only")
                        .child("banned")
                        .get()
                        .addOnSuccessListener { dataSnapshot ->
                            val value = dataSnapshot.getValue(Boolean::class.java) ?: false
                            continuation.resume(value) { throwable ->
                                continuation.resumeWithException(throwable)
                            }
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }
                emit(ReadResponse.IsSuccess(isBanned))
            } catch (e: Exception) {
                emit(ReadResponse.IsFailure(e))
            }
        } else {
            emit(ReadResponse.IsFailure(Exception("User ID is null. Unable to determine banned status.")))
        }
    }

    private inline fun <reified T : FirebaseModel> getFirestoreDocument(databaseReference: DatabaseReference): Flow<ReadResponse<T>> {
        return callbackFlow {

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(T::class.java)?.apply {
                        key = snapshot.key.toString()
                    }
                    if (data != null) {
                        trySend(ReadResponse.IsSuccess(data))
                    } else {
                        trySend(ReadResponse.IsFailure(Exception("The object read is null")))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(ReadResponse.IsFailure(error.toException()))
                }
            }.also {
                databaseReference.addValueEventListener(it)
            }

            awaitClose { databaseReference.removeEventListener(listener) }
        }.catch {
            loggerRepo.error(tag(), it.message.toString())
            emit(ReadResponse.IsFailure(Exception(it.message)))
        }.flowOn(coroutineDispatcher)
    }
}