package com.bbg.securevault.data.objects

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object EncryptedPrefs {
    private fun getPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveEmailAndPassword(context: Context, email: String, password: String) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putString("email", email)
            .putString("password", password)
            .apply()
    }

    fun loadEmailAndPassword(context: Context): Pair<String, String>? {
        val prefs = getPrefs(context)
        val email = prefs.getString("email", null)
        val password = prefs.getString("password", null)
        return if (email != null && password != null) Pair(email, password) else null
    }

    fun clear(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    fun saveLoggedInUserId(context: Context, userId: String) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("stored_user_id", userId).apply()
    }

    fun getLoggedInUserId(context: Context): String? {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("stored_user_id", null)
    }

}