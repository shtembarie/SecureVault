package com.bbg.securevault.presentation.core


import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
/**
 * Created by Enoklit on 04.06.2025.
 */

private val Context.dataStore by preferencesDataStore(name = "secure_vault")

object EncryptionUtils {

    // Simulate encryption
    fun encryptData(data: Any, masterPassword: String): String {
        // In a real app, use AES encryption
        val jsonData = JSONObject(data as Map<*, *>).toString()
        return Base64.encodeToString(jsonData.toByteArray(), Base64.DEFAULT)
    }

    // Simulate decryption
    fun decryptData(encryptedData: String, masterPassword: String): Map<String, Any>? {
        return try {
            val decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
            val jsonString = String(decodedBytes)
            val jsonObject = JSONObject(jsonString)
            val map = mutableMapOf<String, Any>()
            jsonObject.keys().forEach { key ->
                map[key] = jsonObject.get(key)
            }
            map
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Save encrypted data
    fun saveEncryptedData(context: Context, key: String, data: Map<String, Any>, masterPassword: String) {
        val encrypted = encryptData(data, masterPassword)
        val prefKey = stringPreferencesKey(key)
        runBlocking {
            context.dataStore.edit { prefs ->
                prefs[prefKey] = encrypted
            }
        }
    }

    // Load and decrypt data
    fun loadEncryptedData(context: Context, key: String, masterPassword: String): Map<String, Any>? {
        val prefKey = stringPreferencesKey(key)
        return runBlocking {
            val prefs: Preferences = context.dataStore.data.first()
            val encrypted = prefs[prefKey] ?: return@runBlocking null
            decryptData(encrypted, masterPassword)
        }
    }
}

