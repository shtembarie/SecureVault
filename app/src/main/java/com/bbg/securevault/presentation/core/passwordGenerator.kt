package com.bbg.securevault.presentation.core

import com.bbg.securevault.domain.models.PasswordGeneratorOptions

/**
 * Created by Enoklit on 04.06.2025.
 */



object PasswordGenerator {

    private const val UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz"
    private const val NUMBER_CHARS = "0123456789"
    private const val SYMBOL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?"
    private const val SIMILAR_CHARS = "iIlL1oO0"

    fun generatePassword(options: PasswordGeneratorOptions): String {
        var charset = buildString {
            if (options.includeUppercase) append(UPPERCASE_CHARS)
            if (options.includeLowercase) append(LOWERCASE_CHARS)
            if (options.includeNumbers) append(NUMBER_CHARS)
            if (options.includeSymbols) append(SYMBOL_CHARS)
        }

        if (options.excludeSimilarChars) {
            SIMILAR_CHARS.forEach { char ->
                charset = charset.replace(char.toString(), "")
            }
        }

        if (charset.isEmpty()) {
            charset = LOWERCASE_CHARS + NUMBER_CHARS
        }

        val length = options.length.coerceIn(8, 64)
        return buildString {
            repeat(length) {
                val randomChar = charset.random()
                append(randomChar)
            }
        }
    }

    fun calculatePasswordStrength(password: String): String {
        if (password.isEmpty()) return "weak"

        var score = 0

        when {
            password.length >= 12 -> score += 3
            password.length >= 8 -> score += 2
            password.length >= 6 -> score += 1
        }

        if (password.any { it.isUpperCase() }) score += 1
        if (password.any { it.isLowerCase() }) score += 1
        if (password.any { it.isDigit() }) score += 1
        if (password.any { !it.isLetterOrDigit() }) score += 1

        return when {
            score >= 6 -> "strong"
            score >= 4 -> "medium"
            else -> "weak"
        }
    }
}