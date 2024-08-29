package com.tomorrowit.budgetgamer.data.local.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DataStorePreferences(
    private val dataStore: DataStore<Preferences>,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend fun <T> setPreference(key: Preferences.Key<T>, value: T) =
        withContext(coroutineDispatcher) {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }

    fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }.flowOn(coroutineDispatcher)
    }

    suspend fun clearAll() = withContext(coroutineDispatcher) {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}