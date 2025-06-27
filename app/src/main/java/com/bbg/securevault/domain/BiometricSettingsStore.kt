package com.bbg.securevault.domain

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by Enoklit on 11.06.2025.
 */
object BiometricSettingsStore {
    private val Context.dataStore by preferencesDataStore(name = "biometric_prefs")

    private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")

    suspend fun setEnabled(context: Context, enabled: Boolean) {
        context.dataStore.edit { it[BIOMETRIC_ENABLED] = enabled }
    }

    fun isEnabledFlow(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { it[BIOMETRIC_ENABLED] ?: false }
    }
}