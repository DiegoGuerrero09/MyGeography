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
import androidx.compose.ui.draw.scale
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
    onIniciarQuizBanderas: (RegionQuiz, Boolean) -> Unit,
    onIniciarQuizCapitales: (RegionQuiz, Boolean) -> Unit,
    onAbrirListado: () -> Unit,
    onAbrirGlobo: () -> Unit
) {
    val context = LocalContext.current
    val statsManager = remember { EstadisticasManager(context) }

    // Diálogo para seleccionar ámbito (Global o por Continente) y países dependientes
    var tipoQuizSeleccionadoParaDialogo by remember { mutableStateOf<TipoQuiz?>(null) }

    tipoQuizSeleccionadoParaDialogo?.let { tipo ->
        DialogoSeleccionRegion(
            tipoQuiz = tipo,
            onSeleccionarRegion = { region, incluirDependientes ->
                tipoQuizSeleccionadoParaDialogo = null
                if (tipo == TipoQuiz.BANDERAS) {
                    onIniciarQuizBanderas(region, incluirDependientes)
                } else {
                    onIniciarQuizCapitales(region, incluirDependientes)
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
        // Cabecera Principal: Título centrado horizontalmente en el hueco entre el borde izquierdo y el botón del Globo 3D
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenedor centrado horizontalmente entre el borde izquierdo y el botón (ligeramente a la izquierda)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .offset(x = (-10).dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "My Geography",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.3.sp,
                        maxLines = 1
                    )
                }
            }

            // Botón interactivo de acceso al Globo Terráqueo 3D arriba a la derecha
            Surface(
                onClick = onAbrirGlobo,
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, PrimaryCyan.copy(alpha = 0.8f)),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TravelExplore,
                        contentDescription = "Globo terráqueo",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Globo 3D",
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Botón Grande 1: Banderas (254 Países) - Expandido
        BotonModoPrincipal(
            titulo = "Test de banderas",
            subtitulo = "254 PAÍSES",
            descripcion = "Elige la bandera correcta",
            icono = Icons.Default.Flag,
            colorGradienteInicio = Color(0xFF0284C7),
            colorGradienteFin = Color(0xFF0369A1),
            colorAcento = PrimaryBlue,
            onClick = { tipoQuizSeleccionadoParaDialogo = TipoQuiz.BANDERAS },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Botón Grande 2: Capitales (254 Países) - Expandido
        BotonModoPrincipal(
            titulo = "Test de capitales",
            subtitulo = "254 PAÍSES",
            descripcion = "Acierta la capital oficial",
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
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
                            fontSize = 15.sp,
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
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = descripcion,
                        color = TextSecondary,
                        fontSize = 16.5.sp
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
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
                            fontSize = 15.sp,
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
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "País • Capital • Bandera",
                        color = TextSecondary,
                        fontSize = 16.5.sp
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
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
                            fontSize = 15.sp,
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
                        val colorContinente = when (region) {
                            RegionQuiz.GLOBAL -> Color(0xFF94A3B8) // Gris pizarra para Global
                            RegionQuiz.EUROPA -> Color(0xFF38BDF8) // Celeste
                            RegionQuiz.AFRICA -> Color(0xFFF59E0B) // Ámbar cálido
                            RegionQuiz.ASIA -> Color(0xFFEF4444)   // Rojo coral
                            RegionQuiz.OCEANIA -> Color(0xFF10B981) // Verde esmeralda
                            RegionQuiz.SUDAMERICA_ANTARTIDA -> Color(0xFFA855F7) // Púrpura brillante
                            RegionQuiz.NORTEAMERICA_CENTROAMERICA -> Color(0xFF06B6D4) // Cian brillante
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (esActiva) colorContinente.copy(alpha = 0.28f) else Color(0xFF0F172A))
                                .border(
                                    width = if (esActiva) 1.5.dp else 1.dp,
                                    color = if (esActiva) colorContinente else colorContinente.copy(alpha = 0.55f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { regionSeleccionada = region }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = region.abreviatura,
                                color = if (esActiva) colorContinente else Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                fontWeight = if (esActiva) FontWeight.ExtraBold else FontWeight.Medium,
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
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (mejorBanderas >= 0) "$mejorBanderas%" else "-",
                            color = if (mejorBanderas >= 0) PrimaryBlue else TextMuted,
                            fontSize = 19.sp,
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
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (mejorCapitales >= 0) "$mejorCapitales%" else "-",
                            color = if (mejorCapitales >= 0) AccentGold else TextMuted,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Diálogo modal para elegir ámbito al iniciar un test con checkbox para añadir países dependientes.
 * Opciones ordenadas alfabéticamente (Global primero, continentes en orden alfabético).
 */
@Composable
private fun DialogoSeleccionRegion(
    tipoQuiz: TipoQuiz,
    onSeleccionarRegion: (RegionQuiz, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val tituloModo = if (tipoQuiz == TipoQuiz.BANDERAS) "Test de banderas" else "Test de capitales"
    val regionesOrdenadas = remember {
        listOf(RegionQuiz.GLOBAL) + RegionQuiz.values().filter { it != RegionQuiz.GLOBAL }.sortedBy { it.nombre }
    }
    // Checkbox activada en sí (true) por defecto
    var incluirDependientes by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = tituloModo,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Checkbox para añadir Países dependientes o no, activada en sí por defecto
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .border(
                            1.dp,
                            if (incluirDependientes) PrimaryBlue.copy(alpha = 0.5f) else DarkCardBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { incluirDependientes = !incluirDependientes }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Añadir países dependientes",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Checkbox(
                        checked = incluirDependientes,
                        onCheckedChange = { incluirDependientes = it },
                        modifier = Modifier.scale(0.85f),
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryBlue,
                            uncheckedColor = TextSecondary,
                            checkmarkColor = Color.White
                        )
                    )
                }

                HorizontalDivider(
                    color = DarkCardBorder,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                regionesOrdenadas.forEach { region ->
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
                            .clickable { onSeleccionarRegion(region, incluirDependientes) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = region.nombre,
                                color = TextPrimary,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = colorAcento,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary, fontSize = 15.sp)
            }
        }
    )
}
