package com.diegoguerrero.mygeography.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegoguerrero.mygeography.ui.theme.TextMuted

@Composable
fun BanderaImage(
    codigo: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    cornerRadius: Dp = 6.dp,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    elevation: Dp = 0.dp,
    backgroundColor: Color = Color.Transparent,
    contentScale: ContentScale = ContentScale.Fit
) {
    val context = LocalContext.current
    val imageBitmap = remember(codigo) {
        try {
            context.assets.open("flags/${codigo.lowercase()}.png").use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }

    val shape = RoundedCornerShape(cornerRadius)

    val baseModifier = if (elevation > 0.dp) {
        modifier.shadow(elevation, shape)
    } else {
        modifier
    }

    val clippedModifier = baseModifier
        .clip(shape)
        .then(if (backgroundColor != Color.Transparent) Modifier.background(backgroundColor) else Modifier)
        .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, shape) else Modifier)

    Box(
        modifier = clippedModifier,
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = contentDescription ?: "Bandera de $codigo",
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = codigo.uppercase(),
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
