package com.bbg.securevault.presentation.generatorScreen

import androidx.compose.runtime.Composable

/**
 * Created by Enoklit on 04.06.2025.
 */

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.bbg.securevault.R
import com.bbg.securevault.domain.PasswordStore
import com.bbg.securevault.data.models.PasswordGeneratorOptions
import com.bbg.securevault.presentation.components.ButtonSize
import com.bbg.securevault.presentation.components.CustomButton
import com.bbg.securevault.presentation.components.PasswordStrengthIndicator
import com.bbg.securevault.presentation.core.PasswordGenerator


@Composable
fun GeneratorScreen() {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var password by remember { mutableStateOf("") }
    var passwordLength by remember { mutableStateOf(PasswordStore.generatorOptions.length) }
    var generatorOptions by remember { mutableStateOf(PasswordStore.generatorOptions) }

    // Generate password on first load
    LaunchedEffect(Unit) {
        password = PasswordGenerator.generatePassword(generatorOptions.copy(length = passwordLength))
    }

    fun generateNewPassword() {
        password = PasswordGenerator.generatePassword(generatorOptions.copy(length = passwordLength))
    }

    fun handleCopy() {
        clipboardManager.setText(AnnotatedString(password))
        //Toast.makeText(context,
            //context.getString(R.string.password_copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }

    fun handleLengthChange(newLength: Int) {
        val length = newLength.coerceIn(8, 64)
        passwordLength = length
        generatorOptions = generatorOptions.copy(length = length)
        PasswordStore.updateGeneratorOptions(generatorOptions)
        generateNewPassword()
    }

    fun updateToggle(option: PasswordGeneratorOptions.() -> PasswordGeneratorOptions) {
        val updated = generatorOptions.option()

        val typesEnabled = listOf(
            updated.includeUppercase,
            updated.includeLowercase,
            updated.includeNumbers,
            updated.includeSymbols
        ).count { it }

        if (typesEnabled == 0) {
            Toast.makeText(context,
                context.getString(R.string.at_least_one_character_type_must_be_enabled), Toast.LENGTH_SHORT).show()
            return
        }

        generatorOptions = updated
        PasswordStore.updateGeneratorOptions(updated)
        generateNewPassword()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(colorResource(R.color.background))
            .padding(16.dp)
    ) {
        // Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.card))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = password,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(R.color.text),
                    modifier = Modifier.fillMaxWidth()
                )

                PasswordStrengthIndicator(password = password)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomButton(title = stringResource(R.string.copy), onClick = { handleCopy() }, fullWidth = false)
                    CustomButton(title = stringResource(R.string.regenerate), onClick = { generateNewPassword() }, fullWidth = false)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Length
        Text(stringResource(R.string.password_length, passwordLength), color = colorResource(R.color.text), style = MaterialTheme.typography.titleSmall)

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 16.dp)) {
            IconButton(onClick = { handleLengthChange(passwordLength - 1) }, enabled = passwordLength > 8) {
                Text("-", color = colorResource(R.color.primary))
            }

            Slider(
                value = passwordLength.toFloat(),
                onValueChange = { handleLengthChange(it.toInt()) },
                valueRange = 8f..64f,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                steps = 56
            )

            IconButton(onClick = { handleLengthChange(passwordLength + 1) }, enabled = passwordLength < 64) {
                Text("+", color = colorResource(R.color.primary))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggles
        ToggleItem(stringResource(R.string.uppercase_letters_a_z), generatorOptions.includeUppercase) {
            updateToggle { copy(includeUppercase = !includeUppercase) }
        }

        ToggleItem(stringResource(R.string.lowercase_letters_a_z), generatorOptions.includeLowercase) {
            updateToggle { copy(includeLowercase = !includeLowercase) }
        }

        ToggleItem(stringResource(R.string.numbers_0_9), generatorOptions.includeNumbers) {
            updateToggle { copy(includeNumbers = !includeNumbers) }
        }

        ToggleItem(stringResource(R.string.symbols), generatorOptions.includeSymbols) {
            updateToggle { copy(includeSymbols = !includeSymbols) }
        }

        ToggleItem(stringResource(R.string.exclude_similar_characters), generatorOptions.excludeSimilarChars) {
            updateToggle { copy(excludeSimilarChars = !excludeSimilarChars) }
        }

        Spacer(modifier = Modifier.height(24.dp))

        CustomButton(
            title = stringResource(R.string.generate_new_password),
            onClick = { generateNewPassword() },
            fullWidth = true,
            size = ButtonSize.Large
        )
    }
}

@Composable
fun ToggleItem(label: String, isChecked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = colorResource(R.color.text))
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedTrackColor = colorResource(R.color.primary),
                uncheckedTrackColor = colorResource(R.color.border),
                checkedThumbColor = colorResource(R.color.background),
                uncheckedThumbColor = colorResource(R.color.background)
            )
        )
    }
}
