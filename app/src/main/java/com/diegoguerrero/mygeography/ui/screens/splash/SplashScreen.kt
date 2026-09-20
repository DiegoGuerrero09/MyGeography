package com.diegoguerrero.mygeography.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegoguerrero.mygeography.ui.theme.DarkBackground
import com.diegoguerrero.mygeography.ui.theme.PrimaryBlue
import com.diegoguerrero.mygeography.ui.theme.PrimaryCyan
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "globeScale"
    )

    LaunchedEffect(Unit) {
        delay(1600)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1527),
                        DarkBackground,
                        Color(0xFF060A14)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .scale(scale)
                    .background(PrimaryBlue.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .background(PrimaryCyan.copy(alpha = 0.20f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "Globo Terráqueo",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "MyGeography",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Banderas • Capitales • Atlas mundial",
                color = PrimaryBlue.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }
    }
}
