package com.diegoguerrero.mygeography.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegoguerrero.mygeography.data.model.RegionQuiz
import com.diegoguerrero.mygeography.data.model.TipoQuiz
import com.diegoguerrero.mygeography.data.preferences.EstadisticasManager
import com.diegoguerrero.mygeography.ui.theme.*

@Composable
fun MenuScreen(
    onIniciarQuizBanderas: (RegionQuiz) -> Unit,
    onIniciarQuizCapitales: (RegionQuiz) -> Unit,
    onAbrirListado: () -> Unit,
    onAbrirGlobo: () -> Unit
) {
    val context = LocalContext.current
    val statsManager = remember { EstadisticasManager(context) }

    // Diálogo para seleccionar ámbito (Global o por Continente)
    var tipoQuizSeleccionadoParaDialogo by remember { mutableStateOf<TipoQuiz?>(null) }

    tipoQuizSeleccionadoParaDialogo?.let { tipo ->
        DialogoSeleccionRegion(
            tipoQuiz = tipo,
            onSeleccionarRegion = { region ->
                tipoQuizSeleccionadoParaDialogo = null
                if (tipo == TipoQuiz.BANDERAS) {
                    onIniciarQuizBanderas(region)
                } else {
                    onIniciarQuizCapitales(region)
                }
            },
            onDismiss = { tipoQuizSeleccionadoParaDialogo = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Cabecera Principal: Título centrado horizontalmente en la pantalla y botón de Globo Terráqueo arriba a la derecha
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            // Título e Icono centrados horizontalmente en toda la pantalla
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "MyGeography",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }

            // Botón interactivo de acceso al Globo Terráqueo 3D arriba a la derecha
            Surface(
                onClick = onAbrirGlobo,
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                border = androidx.compose.foundation.BorderStroke(1.2.dp, PrimaryCyan.copy(alpha = 0.8f)),
                shadowElevation = 4.dp,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TravelExplore,
                        contentDescription = "Globo terráqueo",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Globo 3D",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Botón Grande 1: Banderas (254 Territorios) - Expandido
        BotonModoPrincipal(
            titulo = "Test de banderas",
            subtitulo = "254 TERRITORIOS",
            descripcion = "Elige la bandera correcta.",
            icono = Icons.Default.Flag,
            colorGradienteInicio = Color(0xFF0284C7),
            colorGradienteFin = Color(0xFF0369A1),
            colorAcento = PrimaryBlue,
            onClick = { tipoQuizSeleccionadoParaDialogo = TipoQuiz.BANDERAS },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Botón Grande 2: Capitales (254 Territorios) - Expandido
        BotonModoPrincipal(
            titulo = "Test de capitales",
            subtitulo = "254 TERRITORIOS",
            descripcion = "Acierta la capital oficial.",
            icono = Icons.Default.LocationCity,
            colorGradienteInicio = Color(0xFFD97706),
            colorGradienteFin = Color(0xFFB45309),
            colorAcento = AccentGold,
            onClick = { tipoQuizSeleccionadoParaDialogo = TipoQuiz.CAPITALES },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Botón 3: Listado de países - Con reborde verde, franja LISTADO y expandido
        BotonListadoPaises(
            onClick = onAbrirListado,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Botón 4: Mini apartado de estadísticas con selector regional - Expandido
        CardEstadisticasConRegiones(
            statsManager = statsManager,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Card(
        modifier = modifier
            .shadow(4.dp, shape)
            .clip(shape)
            .border(1.2.dp, colorAcento.copy(alpha = 0.4f), shape)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = shape
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icono,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(9.dp))
                        Text(
                            text = subtitulo,
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Entrar",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Cuerpo de la tarjeta alineado verticalmente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = titulo,
                        color = TextPrimary,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = descripcion,
                        color = TextSecondary,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BotonListadoPaises(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val colorVerdeAcento = Color(0xFF10B981) // Verde esmeralda

    Card(
        modifier = modifier
            .shadow(4.dp, shape)
            .clip(shape)
            .border(1.2.dp, colorVerdeAcento.copy(alpha = 0.5f), shape)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = shape
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Franja superior con degradado verde y texto LISTADO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF059669), Color(0xFF047857))
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatListNumbered,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(9.dp))
                        Text(
                            text = "LISTADO",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Entrar",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Contenido alineado verticalmente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = "Listado de países",
                        color = TextPrimary,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "País • Capital • Bandera",
                        color = TextSecondary,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CardEstadisticasConRegiones(
    statsManager: EstadisticasManager,
    modifier: Modifier = Modifier
) {
    var regionSeleccionada by remember { mutableStateOf(RegionQuiz.GLOBAL) }
    val mejorBanderas = statsManager.obtenerMejorPorcentaje(TipoQuiz.BANDERAS, regionSeleccionada)
    val mejorCapitales = statsManager.obtenerMejorPorcentaje(TipoQuiz.CAPITALES, regionSeleccionada)
    val shape = RoundedCornerShape(16.dp)
    val colorAmarilloAcento = Color(0xFFEAB308) // Amarillo/Oro

    Card(
        modifier = modifier
            .shadow(4.dp, shape)
            .clip(shape)
            .border(1.2.dp, colorAmarilloAcento.copy(alpha = 0.5f), shape),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = shape
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Franja superior con degradado amarillo y texto PUNTUACIONES
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFCA8A04), Color(0xFFA16207))
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(9.dp))
                        Text(
                            text = "PUNTUACIONES",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Cuerpo con selectores de continente y porcentajes
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Selector horizontal de ámbitos / continentes
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(RegionQuiz.values()) { region ->
                        val esActiva = region == regionSeleccionada
                        val colorFondoActivo = when (region) {
                            RegionQuiz.GLOBAL -> Color(0xFF64748B) // Gris para Global
                            RegionQuiz.EUROPA -> Color(0xFF0284C7)
                            RegionQuiz.AFRICA -> Color(0xFFD97706)
                            RegionQuiz.ASIA -> Color(0xFFDC2626)
                            RegionQuiz.OCEANIA -> Color(0xFF059669)
                            RegionQuiz.SUDAMERICA_ANTARTIDA -> Color(0xFF9333EA)
                            RegionQuiz.NORTEAMERICA_CENTROAMERICA -> Color(0xFF0891B2)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (esActiva) colorFondoActivo else Color(0xFF1E293B))
                                .clickable { regionSeleccionada = region }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = region.abreviatura,
                                color = if (esActiva) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (esActiva) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Estadísticas Banderas
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Banderas",
                            color = TextSecondary,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (mejorBanderas >= 0) "$mejorBanderas%" else "-",
                            color = if (mejorBanderas >= 0) PrimaryBlue else TextMuted,
                            fontSize = 17.5.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Estadísticas Capitales
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Capitales",
                            color = TextSecondary,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (mejorCapitales >= 0) "$mejorCapitales%" else "-",
                            color = if (mejorCapitales >= 0) AccentGold else TextMuted,
                            fontSize = 17.5.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Diálogo modal para elegir ámbito al iniciar un test.
 * Sin texto secundario "Selecciona el ámbito del test".
 */
@Composable
private fun DialogoSeleccionRegion(
    tipoQuiz: TipoQuiz,
    onSeleccionarRegion: (RegionQuiz) -> Unit,
    onDismiss: () -> Unit
) {
    val tituloModo = if (tipoQuiz == TipoQuiz.BANDERAS) "Test de banderas" else "Test de capitales"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = tituloModo,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RegionQuiz.values().forEach { region ->
                    val colorAcento = when (region) {
                        RegionQuiz.GLOBAL -> Color(0xFF94A3B8) // Gris para Global
                        RegionQuiz.EUROPA -> Color(0xFF38BDF8)
                        RegionQuiz.AFRICA -> Color(0xFFF59E0B)
                        RegionQuiz.ASIA -> Color(0xFFEF4444)
                        RegionQuiz.OCEANIA -> Color(0xFF10B981)
                        RegionQuiz.SUDAMERICA_ANTARTIDA -> Color(0xFFA855F7)
                        RegionQuiz.NORTEAMERICA_CENTROAMERICA -> Color(0xFF06B6D4)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, colorAcento.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .clickable { onSeleccionarRegion(region) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = region.nombre,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
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
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}
