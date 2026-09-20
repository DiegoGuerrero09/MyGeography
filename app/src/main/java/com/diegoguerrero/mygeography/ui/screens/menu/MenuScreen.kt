package com.diegoguerrero.mygeography.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegoguerrero.mygeography.ui.theme.*

@Composable
fun MenuScreen(
    onIniciarQuizBanderas: () -> Unit,
    onIniciarQuizCapitales: () -> Unit,
    onAbrirListado: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Cabecera Principal
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "MyGeography",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Desafía tus conocimientos mundiales",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Grande 1: BANDERAS (254 Naciones)
        BotonModoPrincipal(
            titulo = "Test de Banderas",
            subtitulo = "254 NACIONES Y TERRITORIOS",
            descripcion = "Se te presentará el nombre de una nación y deberás elegir la bandera correcta en una cuadrícula 3x3.",
            icono = Icons.Default.Flag,
            colorGradienteInicio = Color(0xFF0284C7),
            colorGradienteFin = Color(0xFF0369A1),
            colorAcento = PrimaryBlue,
            onClick = onIniciarQuizBanderas
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botón Grande 2: CAPITALES (195 Soberanos)
        BotonModoPrincipal(
            titulo = "Test de Capitales",
            subtitulo = "195 PAÍSES SOBERANOS",
            descripcion = "Se te presentará el país con su bandera y deberás acertar su capital oficial entre 9 opciones en una cuadrícula 3x3.",
            icono = Icons.Default.LocationCity,
            colorGradienteInicio = Color(0xFFD97706),
            colorGradienteFin = Color(0xFFB45309),
            colorAcento = AccentGold,
            onClick = onIniciarQuizCapitales
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Listado de País - Capital - Bandera
        BotonListadoPaises(onClick = onAbrirListado)

        Spacer(modifier = Modifier.height(28.dp))

        // Nota al pie
        Text(
            text = "Datos completos • 254 banderas de banderas-mundo.es",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun BotonModoPrincipal(
    titulo: String,
    subtitulo: String,
    descripcion: String,
    icono: ImageVector,
    colorGradienteInicio: Color,
    colorGradienteFin: Color,
    colorAcento: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape)
            .clip(shape)
            .border(1.5.dp, colorAcento.copy(alpha = 0.35f), shape)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = shape
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Franja superior con degradado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(colorGradienteInicio, colorGradienteFin)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icono,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = subtitulo,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Entrar",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Cuerpo de la tarjeta
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = titulo,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = descripcion,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón visual de acción
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorAcento.copy(alpha = 0.15f))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COMENZAR TEST",
                        color = colorAcento,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = colorAcento,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BotonListadoPaises(
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape)
            .clip(shape)
            .border(1.dp, DarkCardBorder, shape)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FormatListNumbered,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Listado de Países",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "País • Capital • Bandera (Atlas completo)",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
