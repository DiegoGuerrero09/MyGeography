package com.diegoguerrero.mygeography.ui.screens.resumen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegoguerrero.mygeography.data.model.RegionQuiz
import com.diegoguerrero.mygeography.data.model.RespuestaQuiz
import com.diegoguerrero.mygeography.data.model.TipoQuiz
import com.diegoguerrero.mygeography.data.preferences.EstadisticasManager
import com.diegoguerrero.mygeography.data.repository.PaisesRepository
import com.diegoguerrero.mygeography.ui.components.BanderaImage
import com.diegoguerrero.mygeography.ui.screens.quiz.TextoAjustable
import com.diegoguerrero.mygeography.ui.theme.*

private enum class FiltroResultados(val titulo: String) {
    TODAS("Todas"),
    ACERTADAS("Acertadas"),
    FALLADAS("Falladas")
}

@Composable
fun ResultadosScreen(
    tipoQuiz: TipoQuiz,
    region: RegionQuiz = RegionQuiz.GLOBAL,
    respuestas: List<RespuestaQuiz>,
    onVolverAlMenu: () -> Unit,
    onReiniciarQuiz: () -> Unit
) {
    var filtroSeleccionado by remember { mutableStateOf(FiltroResultados.TODAS) }

    val total = respuestas.size
    val acertadas = respuestas.count { it.esCorrecta }
    val falladas = respuestas.count { !it.esCorrecta }
    val porcentaje = if (total > 0) ((acertadas.toFloat() / total.toFloat()) * 100).toInt() else 0

    // Guardar el récord en estadísticas
    val context = LocalContext.current
    val statsManager = remember { EstadisticasManager(context) }
    val repository = remember { PaisesRepository() }

    LaunchedEffect(porcentaje) {
        // Guardar el porcentaje del ámbito jugado
        statsManager.guardarPorcentaje(tipoQuiz, region, porcentaje)

        // Si se jugó el test global, calcular y actualizar también los récords de cada continente
        if (region == RegionQuiz.GLOBAL) {
            RegionQuiz.values().filter { it != RegionQuiz.GLOBAL }.forEach { regionItem ->
                val respuestasRegion = respuestas.filter {
                    repository.perteneceARegion(it.pregunta.paisCorrecto, regionItem)
                }
                if (respuestasRegion.isNotEmpty()) {
                    val aciertosRegion = respuestasRegion.count { it.esCorrecta }
                    val porcentajeRegion = ((aciertosRegion.toFloat() / respuestasRegion.size.toFloat()) * 100).toInt()
                    statsManager.guardarPorcentaje(tipoQuiz, regionItem, porcentajeRegion)
                }
            }
        }
    }

    val respuestasFiltradas = remember(filtroSeleccionado, respuestas) {
        when (filtroSeleccionado) {
            FiltroResultados.TODAS -> respuestas
            FiltroResultados.ACERTADAS -> respuestas.filter { it.esCorrecta }
            FiltroResultados.FALLADAS -> respuestas.filter { !it.esCorrecta }
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            // Barra inferior con botón para volver al menú
            Surface(
                color = DarkCard,
                tonalElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onReiniciarQuiz,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryBlue),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Repetir", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Button(
                        onClick = onVolverAlMenu,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Volver al menú",
                            color = DarkBackground,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Tarjeta de Resumen General
                CardResumenGeneral(
                    tipoQuiz = tipoQuiz,
                    region = region,
                    total = total,
                    acertadas = acertadas,
                    falladas = falladas,
                    porcentaje = porcentaje
                )
            }

            item {
                // Selector de pestañas para filtrar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FiltroResultados.values().forEach { filtro ->
                        val cantidad = when (filtro) {
                            FiltroResultados.TODAS -> total
                            FiltroResultados.ACERTADAS -> acertadas
                            FiltroResultados.FALLADAS -> falladas
                        }

                        val esSeleccionado = filtro == filtroSeleccionado
                        val bg = if (esSeleccionado) PrimaryBlue else Color.Transparent
                        val textCol = if (esSeleccionado) DarkBackground else TextSecondary

                        Button(
                            onClick = { filtroSeleccionado = filtro },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = bg,
                                contentColor = textCol
                            ),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text(
                                text = "${filtro.titulo} ($cantidad)",
                                fontSize = 12.sp,
                                fontWeight = if (esSeleccionado) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Detalle de respuestas (${respuestasFiltradas.size})",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Si no hay aciertos o fallos, mostrar un mensaje claro
            if (respuestasFiltradas.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (filtroSeleccionado) {
                                    FiltroResultados.ACERTADAS -> "No tuviste ningún acierto en este test."
                                    FiltroResultados.FALLADAS -> "¡Excelente! No cometiste ningún fallo en este test."
                                    else -> "No hay respuestas disponibles."
                                },
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            } else {
                items(respuestasFiltradas) { itemRespuesta ->
                    ItemDetalleRespuesta(
                        tipoQuiz = tipoQuiz,
                        respuesta = itemRespuesta
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CardResumenGeneral(
    tipoQuiz: TipoQuiz,
    region: RegionQuiz,
    total: Int,
    acertadas: Int,
    falladas: Int,
    porcentaje: Int
) {
    val shape = RoundedCornerShape(20.dp)
    val tituloModo = if (region == RegionQuiz.GLOBAL) {
        if (tipoQuiz == TipoQuiz.BANDERAS) "Test de banderas" else "Test de capitales"
    } else {
        val base = if (tipoQuiz == TipoQuiz.BANDERAS) "Test de banderas" else "Test de capitales"
        "$base • ${region.nombre}"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape)
            .border(1.5.dp, PrimaryBlue.copy(alpha = 0.4f), shape),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = shape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AccentGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = AccentGold,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Resumen del test",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = tituloModo,
                color = PrimaryBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Gran porcentaje
            Text(
                text = "$porcentaje%",
                color = when {
                    porcentaje >= 80 -> CorrectGreen
                    porcentaje >= 50 -> AccentGold
                    else -> WrongRed
                },
                fontSize = 42.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = "$acertadas de $total preguntas acertadas",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Dos bloques: Acertadas y Falladas (texto centrado vertical y horizontalmente)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bloque Acertadas
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, CorrectGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CorrectGreenBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = CorrectGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$acertadas",
                                color = CorrectGreen,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Acertadas",
                            color = CorrectGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Bloque Falladas
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, WrongRedBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = WrongRedBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = WrongRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$falladas",
                                color = WrongRed,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Falladas",
                            color = WrongRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemDetalleRespuesta(
    tipoQuiz: TipoQuiz,
    respuesta: RespuestaQuiz
) {
    val paisCorrecto = respuesta.pregunta.paisCorrecto
    val shape = RoundedCornerShape(14.dp)
    val colorBorde = if (respuesta.esCorrecta) CorrectGreenBorder.copy(alpha = 0.4f) else WrongRedBorder.copy(alpha = 0.4f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colorBorde, shape),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bandera del país sin reborde (tamaño fijo)
            BanderaImage(
                codigo = paisCorrecto.codigo,
                modifier = Modifier
                    .size(width = 68.dp, height = 45.dp),
                borderWidth = 0.dp,
                elevation = 0.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Información de la pregunta y respuesta
            Column(modifier = Modifier.weight(1f)) {
                TextoAjustable(
                    texto = paisCorrecto.nombre,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    baseSize = 14
                )

                when (tipoQuiz) {
                    TipoQuiz.BANDERAS -> {
                        if (respuesta.esCorrecta) {
                            Text(
                                text = "¡Bandera acertada!",
                                color = CorrectGreen,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "Elegiste: ",
                                    color = WrongRed,
                                    fontSize = 11.5.sp
                                )
                                BanderaImage(
                                    codigo = respuesta.opcionSeleccionada.codigo,
                                    modifier = Modifier
                                        .size(width = 24.dp, height = 16.dp),
                                    borderWidth = 0.dp,
                                    elevation = 0.dp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(${respuesta.opcionSeleccionada.nombre})",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    TipoQuiz.CAPITALES -> {
                        if (respuesta.esCorrecta) {
                            Text(
                                text = "Capital: ${paisCorrecto.capital}",
                                color = CorrectGreen,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Column(modifier = Modifier.padding(top = 2.dp)) {
                                Text(
                                    text = "Elegiste: ${respuesta.opcionSeleccionada.capital}",
                                    color = WrongRed,
                                    fontSize = 11.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Correcta: ${paisCorrecto.capital}",
                                    color = CorrectGreen,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Icono de estado fijo
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (respuesta.esCorrecta) CorrectGreenBg else WrongRedBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (respuesta.esCorrecta) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (respuesta.esCorrecta) CorrectGreen else WrongRed,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
