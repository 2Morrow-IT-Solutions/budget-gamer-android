package com.tomorrowit.budgetgamer.data.repository_impl

import androidx.datastore.preferences.core.stringPreferencesKey
import com.tomorrowit.budgetgamer.common.config.Constants.Preferences.NOTIFICATION_PREFERENCE_KEY
import com.tomorrowit.budgetgamer.common.config.extensions.tag
import com.tomorrowit.budgetgamer.data.local.prefs.DataStorePreferences
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo
import com.tomorrowit.budgetgamer.domain.repo.PreferencesRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepoImpl @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val loggerRepo: LoggerRepo
) : PreferencesRepo {

    private val notificationIdPreference by lazy { stringPreferencesKey(NOTIFICATION_PREFERENCE_KEY) }

    override fun getMessagingToken(): Flow<String> {
        loggerRepo.debug(tag(), "getMessagingToken")
        return dataStorePreferences.getPreference(notificationIdPreference, "")
    }

    override suspend fun setMessagingToken(token: String) {
        loggerRepo.debug(tag(), "setMessagingToken")
        dataStorePreferences.setPreference(notificationIdPreference, token)
    }

    override suspend fun clearAll() {
        loggerRepo.debug(tag(), "clearAll")
        dataStorePreferences.clearAll()
    }
}