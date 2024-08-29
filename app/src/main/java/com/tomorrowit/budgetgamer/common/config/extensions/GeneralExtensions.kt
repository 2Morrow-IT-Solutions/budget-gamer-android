package com.tomorrowit.budgetgamer.common.config.extensions

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers

fun Any.tag(): String = this.javaClass.simpleName

val Dispatchers.UI get() = Dispatchers.Main.immediate

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "local_prefs")
