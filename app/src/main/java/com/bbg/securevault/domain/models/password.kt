package com.bbg.securevault.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Enoklit on 04.06.2025.
 */
@Entity(tableName = "passwords")
data class PasswordEntry(
    @PrimaryKey val id: String,
    val title: String,
    val username: String,
    val password: String,
    val url: String? = null,
    val notes: String? = null,
    val category: PasswordCategory,
    val favorite: Boolean,
    val lastModified: Long, // timestamp in millis
    val createdAt: Long // timestamp in millis
)

enum class PasswordCategory {
    PERSONAL,
    WORK,
    FINANCE,
    SOCIAL,
    OTHER
}

data class PasswordGeneratorOptions(
    val includeUppercase: Boolean = true,
    val includeLowercase: Boolean = true,
    val includeNumbers: Boolean = true,
    val includeSymbols: Boolean = true,
    val excludeSimilarChars: Boolean = false,
    val length: Int = 12
)

enum class PasswordStrength {
    WEAK,
    MEDIUM,
    STRONG
}