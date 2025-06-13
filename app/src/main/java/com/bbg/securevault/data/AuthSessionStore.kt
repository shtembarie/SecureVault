package com.bbg.securevault.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by Enoklit on 11.06.2025.
 */
object AuthSessionStore {
    private val Context.dataStore by preferencesDataStore("auth_prefs")
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

    suspend fun setLoggedIn(context: Context, loggedIn: Boolean) {
        context.dataStore.edit { it[IS_LOGGED_IN] = loggedIn }
    }

    fun isLoggedInFlow(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    }
}