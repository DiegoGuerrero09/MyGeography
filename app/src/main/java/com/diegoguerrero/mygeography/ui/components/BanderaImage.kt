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
import com.diegoguerrero.mygeography.ui.theme.DarkCardBorder
import com.diegoguerrero.mygeography.ui.theme.TextMuted

@Composable
fun BanderaImage(
    codigo: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    cornerRadius: Dp = 8.dp,
    borderColor: Color = DarkCardBorder,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 2.dp,
    contentScale: ContentScale = ContentScale.Crop
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

    Box(
        modifier = modifier
            .shadow(elevation, shape)
            .clip(shape)
            .background(Color(0xFF161F33))
            .border(borderWidth, borderColor, shape),
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
