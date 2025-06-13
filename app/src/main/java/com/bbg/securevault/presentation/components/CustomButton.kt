package com.bbg.securevault.presentation.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bbg.securevault.R

/**
 * Created by Enoklit on 04.06.2025.
 */

enum class ButtonVariant { Primary, Secondary, Outline, Danger }
enum class ButtonSize { Small, Medium, Large }

@Composable
fun CustomButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    fullWidth: Boolean = false,
    disabled: Boolean = false,
    loading: Boolean = false
) {
    val backgroundColor: Color
    val contentColor: Color
    val border: BorderStroke?

    when (variant) {
        ButtonVariant.Primary -> {
            backgroundColor = colorResource(R.color.primary)
            contentColor = colorResource(R.color.background)
            border = null
        }
        ButtonVariant.Secondary -> {
            backgroundColor = colorResource(R.color.secondary)
            contentColor = colorResource(R.color.background)
            border = null
        }
        ButtonVariant.Outline -> {
            backgroundColor = Color.Transparent
            contentColor = colorResource(R.color.primary)
            border = BorderStroke(1.dp, colorResource(R.color.primary))
        }
        ButtonVariant.Danger -> {
            backgroundColor = colorResource(R.color.danger)
            contentColor = colorResource(R.color.background)
            border = null
        }
    }

    val padding = when (size) {
        ButtonSize.Small -> PaddingValues(vertical = 6.dp, horizontal = 12.dp)
        ButtonSize.Medium -> PaddingValues(vertical = 10.dp, horizontal = 16.dp)
        ButtonSize.Large -> PaddingValues(vertical = 14.dp, horizontal = 20.dp)
    }

    val fontSize = when (size) {
        ButtonSize.Small -> 12.sp
        ButtonSize.Medium -> 14.sp
        ButtonSize.Large -> 16.sp
    }

    val shape = RoundedCornerShape(8.dp)

    Button(
        onClick = onClick,
        modifier = if (fullWidth) modifier.fillMaxWidth() else modifier,
        enabled = !disabled && !loading,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = colorResource(R.color.border),
            disabledContentColor = colorResource(R.color.textSecondary)
        ),
        contentPadding = padding,
        shape = shape,
        border = border
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = if (variant == ButtonVariant.Outline) colorResource(R.color.primary) else colorResource(R.color.background),
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Text(text = title, fontSize = fontSize)
        }
    }
}