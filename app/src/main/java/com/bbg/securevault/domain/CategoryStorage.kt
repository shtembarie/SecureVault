package com.bbg.securevault.domain

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

/**
 * Created by Enoklit on 24.06.2025.
 */
object CategoryStorage {
    private val Context.dataStore by preferencesDataStore(name = "category_prefs")
    private val CUSTOM_CATEGORIES_KEY = stringSetPreferencesKey("custom_categories")

    suspend fun getCustomCategories(context: Context): List<String> {
        val prefs = context.dataStore.data.first()
        return prefs[CUSTOM_CATEGORIES_KEY]?.toList() ?: emptyList()
    }

    suspend fun saveCustomCategories(context: Context, categories: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[CUSTOM_CATEGORIES_KEY] = categories.toSet()
        }
    }
}
