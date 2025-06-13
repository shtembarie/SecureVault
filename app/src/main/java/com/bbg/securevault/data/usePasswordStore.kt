package com.bbg.securevault.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bbg.securevault.data.local.EncryptedPasswordDatabase
import com.bbg.securevault.data.local.repository.PasswordRepository
import com.bbg.securevault.domain.models.PasswordEntry
import com.bbg.securevault.domain.models.PasswordGeneratorOptions
import com.bbg.securevault.presentation.core.PasswordGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
/**
 * Created by Enoklit on 04.06.2025.
 */
object PasswordStore {

    // Auth state
    var isAuthenticated by mutableStateOf(false)
    var masterPassword: String? = null

    // Password list (für UI)
    var passwords by mutableStateOf(listOf<PasswordEntry>())

    // Generator options
    var generatorOptions by mutableStateOf(
        PasswordGeneratorOptions(
            length = 16,
            includeUppercase = true,
            includeLowercase = true,
            includeNumbers = true,
            includeSymbols = true,
            excludeSimilarChars = false
        )
    )

    // Repository mit SQLCipher
    lateinit var repository: PasswordRepository

    fun setAuthenticated(context: Context, authenticated: Boolean, password: String) {
        isAuthenticated = authenticated
        masterPassword = password
        repository = PasswordRepository(
            EncryptedPasswordDatabase.getInstance(context, password)
        )
    }

    suspend fun loadFromDatabase() {
        passwords = withContext(Dispatchers.IO) {
            repository.getAllPasswords()
        }
    }

    suspend fun addPassword(entry: PasswordEntry) {
        val newEntry = entry.copy(
            id = UUID.randomUUID().toString(),
            createdAt = System.currentTimeMillis(),
            lastModified = System.currentTimeMillis()
        )
        withContext(Dispatchers.IO) {
            repository.insertPassword(newEntry)
        }
        loadFromDatabase()
    }

    suspend fun updatePassword(id: String, updates: PasswordEntry) {
        val updated = updates.copy(
            id = id,
            lastModified = System.currentTimeMillis()
        )
        withContext(Dispatchers.IO) {
            repository.insertPassword(updated)
        }
        loadFromDatabase()
    }

    suspend fun deletePassword(id: String) {
        val target = passwords.find { it.id == id } ?: return
        withContext(Dispatchers.IO) {
            repository.deletePassword(target)
        }
        loadFromDatabase()
    }

    suspend fun toggleFavorite(id: String) {
        val current = repository.getPasswordById(id) ?: return
        val updated = current.copy(favorite = !current.favorite)
        repository.insertPassword(updated)
    }


    fun updateGeneratorOptions(newOptions: PasswordGeneratorOptions) {
        generatorOptions = newOptions
    }

    fun generateNewPassword(): String {
        return PasswordGenerator.generatePassword(generatorOptions)
    }

    fun logout() {
        isAuthenticated = false
        masterPassword = null
        passwords = emptyList()
    }
}

