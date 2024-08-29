package com.tomorrowit.budgetgamer.data.repository_impl

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.tomorrowit.budgetgamer.common.di.IoDispatcher
import com.tomorrowit.budgetgamer.data.model.ArticleModel
import com.tomorrowit.budgetgamer.data.model.FirebaseModel
import com.tomorrowit.budgetgamer.data.model.GameModel
import com.tomorrowit.budgetgamer.domain.repo.CollectionRepo
import com.tomorrowit.budgetgamer.domain.repo.CollectionResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CollectionRepoImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : CollectionRepo {

    override fun getAllArticles(): Flow<CollectionResult<List<ArticleModel>>> {
        val query = firebaseDatabase.getReference("articles").orderByChild("end_date")
        return queryCollection<ArticleModel>(query)
    }

    override fun getAllFreeGames(): Flow<CollectionResult<List<GameModel>>> {
        val query = firebaseDatabase.getReference("free_games").orderByChild("end_date")
        return queryCollection<GameModel>(query)
    }

    override fun getAllSubscriptionGames(): Flow<CollectionResult<List<GameModel>>> {
        val query = firebaseDatabase.getReference("subscription_games")
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.children.flatMap { platformSnapshot ->
                        val subscriptionId = platformSnapshot.key ?: ""
                        platformSnapshot.children.mapNotNull { gameSnapshot ->
                            gameSnapshot.getValue(GameModel::class.java)?.apply {
                                key = gameSnapshot.key.toString()
                                subscription_id = subscriptionId
                            }
                        }
                    }
                    trySend(CollectionResult.IsSuccess(data))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(CollectionResult.IsFailure(error.toException()))
                }
            }

            query.addValueEventListener(listener)

            awaitClose { query.removeEventListener(listener) }
        }.catch {
            emit(CollectionResult.IsFailure(Exception(it.message)))
        }.flowOn(coroutineDispatcher)
    }

    private inline fun <reified T : FirebaseModel> queryCollection(
        query: Query
    ): Flow<CollectionResult<List<T>>> {
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.children.mapNotNull { document ->
                        document.getValue(T::class.java)?.apply {
                            key = document.key.toString()
                        }
                    }
                    trySend(CollectionResult.IsSuccess(data))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(CollectionResult.IsFailure(error.toException()))
                }
            }

            query.addValueEventListener(listener)

            awaitClose { query.removeEventListener(listener) }
        }.catch {
            emit(CollectionResult.IsFailure(Exception(it.message)))
        }.flowOn(coroutineDispatcher)
    }
}