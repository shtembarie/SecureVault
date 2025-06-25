package com.bbg.securevault.domain.local.repository

import android.content.Context
import com.bbg.securevault.domain.local.EncryptedPasswordDatabase
import com.bbg.securevault.data.models.PasswordEntry


/**
 * Created by Enoklit on 13.06.2025.
 */
class PasswordRepository(
    private val db: EncryptedPasswordDatabase
) {
    private val dao = db.passwordDao()

    suspend fun getAllPasswords(): List<PasswordEntry> {
        return dao.getAll()
    }

    suspend fun insertPassword(entry: PasswordEntry) {
        dao.insert(entry)
    }

    suspend fun deletePassword(entry: PasswordEntry) {
        dao.delete(entry)
    }

    suspend fun clearAllPasswords() {
        dao.clearAll()
    }
    suspend fun getPasswordById(id: String): PasswordEntry? {
        return dao.getById(id)
    }
    // MODIFY: Accept userId parameter
    suspend fun getAllPasswordsUsers(userId: String): List<PasswordEntry> {
        return dao.getAllForUser(userId)
    }


    companion object {
        @Volatile
        private var INSTANCE: PasswordRepository? = null

        fun initialize(context: Context, masterPassword: String) {
            if (INSTANCE == null) {
                synchronized(this) {
                    val db = EncryptedPasswordDatabase.getInstance(context, masterPassword)
                    INSTANCE = PasswordRepository(db)
                }
            }
        }

        fun get(): PasswordRepository {
            return INSTANCE ?: throw IllegalStateException("Repository not initialized. Call initialize() first.")
        }
    }
}