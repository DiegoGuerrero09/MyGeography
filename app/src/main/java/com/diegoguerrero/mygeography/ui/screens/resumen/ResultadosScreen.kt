package com.diegoguerrero.mygeography.ui.screens.resumen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.diegoguerrero.mygeography.data.model.Continente
import com.diegoguerrero.mygeography.data.model.Pais
import com.diegoguerrero.mygeography.data.model.RegionQuiz
import com.diegoguerrero.mygeography.data.model.RespuestaQuiz
import com.diegoguerrero.mygeography.data.model.TipoQuiz
import com.diegoguerrero.mygeography.data.preferences.EstadisticasManager
import com.diegoguerrero.mygeography.data.repository.PaisesRepository
import com.diegoguerrero.mygeography.ui.components.BanderaImage
import com.diegoguerrero.mygeography.ui.screens.quiz.TextoAjustable
import com.diegoguerrero.mygeography.ui.theme.*

private enum class FiltroCategoriaResultados(val label: String) {
    GLOBAL("Global"),
    AFRICA("África"),
    ASIA("Asia"),
    EUROPA("Europa"),
    NORTEAMERICA_CENTROAMERICA("Norteamérica & Centroamérica"),
    OCEANIA("Oceanía"),
    SUDAMERICA_ANTARTIDA("Sudamérica & Antártida"),
    INDEPENDIENTES("Independientes"),
    DEPENDIENTES("Dependientes")
}

private fun colorCategoriaResultados(cat: FiltroCategoriaResultados, regionQuiz: RegionQuiz = RegionQuiz.GLOBAL): Color {
    return when (cat) {
        FiltroCategoriaResultados.GLOBAL -> {
            if (regionQuiz == RegionQuiz.GLOBAL) Color(0xFF94A3B8) else colorRegion(regionQuiz)
        }
        FiltroCategoriaResultados.AFRICA -> Color(0xFFF59E0B) // Ámbar cálido
        FiltroCategoriaResultados.ASIA -> Color(0xFFEF4444)   // Rojo coral
        FiltroCategoriaResultados.EUROPA -> Color(0xFF38BDF8) // Celeste
        FiltroCategoriaResultados.NORTEAMERICA_CENTROAMERICA -> Color(0xFF06B6D4) // Cian brillante
        FiltroCategoriaResultados.OCEANIA -> Color(0xFF10B981) // Verde esmeralda
        FiltroCategoriaResultados.SUDAMERICA_ANTARTIDA -> Color(0xFFA855F7) // Púrpura brillante
        FiltroCategoriaResultados.INDEPENDIENTES -> Color(0xFFFACC15) // Dorado soberano
        FiltroCategoriaResultados.DEPENDIENTES -> Color(0xFFCBD5E1) // Gris territorio
    }
}

private fun colorRegion(region: RegionQuiz): Color {
    return when (region) {
        RegionQuiz.GLOBAL -> Color(0xFF94A3B8)
        RegionQuiz.EUROPA -> Color(0xFF38BDF8)
        RegionQuiz.AFRICA -> Color(0xFFF59E0B)
        RegionQuiz.ASIA -> Color(0xFFEF4444)
        RegionQuiz.OCEANIA -> Color(0xFF10B981)
        RegionQuiz.SUDAMERICA_ANTARTIDA -> Color(0xFFA855F7)
        RegionQuiz.NORTEAMERICA_CENTROAMERICA -> Color(0xFF06B6D4)
    }
}

private fun coincideCategoria(
    pais: Pais,
    filtro: FiltroCategoriaResultados,
    repository: PaisesRepository
): Boolean {
    return when (filtro) {
        FiltroCategoriaResultados.GLOBAL -> true
        FiltroCategoriaResultados.AFRICA -> repository.perteneceARegion(pais, RegionQuiz.AFRICA)
        FiltroCategoriaResultados.ASIA -> repository.perteneceARegion(pais, RegionQuiz.ASIA)
        FiltroCategoriaResultados.EUROPA -> repository.perteneceARegion(pais, RegionQuiz.EUROPA)
        FiltroCategoriaResultados.NORTEAMERICA_CENTROAMERICA -> repository.perteneceARegion(pais, RegionQuiz.NORTEAMERICA_CENTROAMERICA)
        FiltroCategoriaResultados.OCEANIA -> repository.perteneceARegion(pais, RegionQuiz.OCEANIA)
        FiltroCategoriaResultados.SUDAMERICA_ANTARTIDA -> repository.perteneceARegion(pais, RegionQuiz.SUDAMERICA_ANTARTIDA)
        FiltroCategoriaResultados.INDEPENDIENTES -> pais.esSoberano
        FiltroCategoriaResultados.DEPENDIENTES -> !pais.esSoberano
    }
}

private fun textoBotonCategoria(cat: FiltroCategoriaResultados, region: RegionQuiz): String {
    return if (cat == FiltroCategoriaResultados.GLOBAL && region != RegionQuiz.GLOBAL) {
        "Todos"
    } else {
        cat.label
    }
}

private enum class FiltroResultados(val titulo: String) {
    TODAS("Todas"),
    ACERTADAS("Acertadas"),
    FALLADAS("Falladas"),
    FALLO_BANDERA("Fallos de bandera"),
    FALLO_CAPITAL("Fallos de capital")
}

@Composable
fun ResultadosScreen(
    tipoQuiz: TipoQuiz,
    region: RegionQuiz = RegionQuiz.GLOBAL,
    respuestas: List<RespuestaQuiz>,
    onVolverAlMenu: () -> Unit,
    onReiniciarQuiz: () -> Unit
) {
    var filtroCategoria by remember { mutableStateOf(FiltroCategoriaResultados.GLOBAL) }
    var filtroSeleccionado by remember { mutableStateOf(FiltroResultados.TODAS) }

    val totalGlobal = respuestas.size
    val acertadasGlobal = respuestas.count { it.esCorrecta }
    val porcentajeGlobal = if (totalGlobal > 0) ((acertadasGlobal.toFloat() / totalGlobal.toFloat()) * 100).toInt() else 0

    // Guardar el récord en estadísticas
    val context = LocalContext.current
    val statsManager = remember { EstadisticasManager(context) }
    val repository = remember { PaisesRepository() }

    LaunchedEffect(porcentajeGlobal) {
        // Guardar el porcentaje del ámbito jugado
        statsManager.guardarPorcentaje(tipoQuiz, region, porcentajeGlobal)

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

    // Si el test es global, permitir filtrar por continente, independientes o dependientes.
    // Si no es global, solo por dependientes e independientes.
    val categoriasDisponibles = remember(region) {
        if (region == RegionQuiz.GLOBAL) {
            listOf(
                FiltroCategoriaResultados.GLOBAL,
                FiltroCategoriaResultados.AFRICA,
                FiltroCategoriaResultados.ASIA,
                FiltroCategoriaResultados.EUROPA,
                FiltroCategoriaResultados.NORTEAMERICA_CENTROAMERICA,
                FiltroCategoriaResultados.OCEANIA,
                FiltroCategoriaResultados.SUDAMERICA_ANTARTIDA,
                FiltroCategoriaResultados.INDEPENDIENTES,
                FiltroCategoriaResultados.DEPENDIENTES
            )
        } else {
            listOf(
                FiltroCategoriaResultados.GLOBAL,
                FiltroCategoriaResultados.INDEPENDIENTES,
                FiltroCategoriaResultados.DEPENDIENTES
            )
        }
    }

    // Filtrar respuestas por la categoría seleccionada
    val respuestasPorCategoria = remember(filtroCategoria, respuestas) {
        respuestas.filter { coincideCategoria(it.pregunta.paisCorrecto, filtroCategoria, repository) }
    }

    val totalCategoria = respuestasPorCategoria.size
    val acertadasCategoria = respuestasPorCategoria.count { it.esCorrecta }
    val falladasCategoria = respuestasPorCategoria.count { !it.esCorrecta }
    val fallosBanderaCategoria = respuestasPorCategoria.count { !it.esCorrecta && it.falloEnBandera }
    val fallosCapitalCategoria = respuestasPorCategoria.count { !it.esCorrecta && !it.falloEnBandera }
    val porcentajeCategoria = if (totalCategoria > 0) ((acertadasCategoria.toFloat() / totalCategoria.toFloat()) * 100).toInt() else 0

    // Filtrar por el estado de acierto/fallo dentro de la categoría
    val respuestasFiltradas = remember(filtroSeleccionado, respuestasPorCategoria) {
        when (filtroSeleccionado) {
            FiltroResultados.TODAS -> respuestasPorCategoria
            FiltroResultados.ACERTADAS -> respuestasPorCategoria.filter { it.esCorrecta }
            FiltroResultados.FALLADAS -> respuestasPorCategoria.filter { !it.esCorrecta }
            FiltroResultados.FALLO_BANDERA -> respuestasPorCategoria.filter { !it.esCorrecta && it.falloEnBandera }
            FiltroResultados.FALLO_CAPITAL -> respuestasPorCategoria.filter { !it.esCorrecta && !it.falloEnBandera }
        }
    }

    val colorTema = when (tipoQuiz) {
        TipoQuiz.BANDERAS -> PrimaryBlue
        TipoQuiz.CAPITALES -> AccentGold
        TipoQuiz.MIXTO -> Color(0xFFEF4444)
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
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, colorTema),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorTema)
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
                        colors = ButtonDefaults.buttonColors(containerColor = colorTema)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = if (tipoQuiz == TipoQuiz.MIXTO) Color.White else DarkBackground,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Volver al menú",
                            color = if (tipoQuiz == TipoQuiz.MIXTO) Color.White else DarkBackground,
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
                // Tarjeta de Resumen General recalculada con la categoría seleccionada
                CardResumenGeneral(
                    tipoQuiz = tipoQuiz,
                    region = region,
                    categoriaSeleccionada = filtroCategoria,
                    total = totalCategoria,
                    acertadas = acertadasCategoria,
                    falladas = falladasCategoria,
                    fallosBandera = fallosBanderaCategoria,
                    fallosCapital = fallosCapitalCategoria,
                    porcentaje = porcentajeCategoria
                )
            }

            // Selectores horizontales de categorías (continentes y soberanía)
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categoriasDisponibles) { cat ->
                        val esActivo = cat == filtroCategoria
                        val colorCat = colorCategoriaResultados(cat, region)
                        val textoBoton = textoBotonCategoria(cat, region)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (esActivo) colorCat.copy(alpha = 0.28f) else Color(0xFF0F172A))
                                .border(
                                    width = if (esActivo) 1.5.dp else 1.dp,
                                    color = if (esActivo) colorCat else colorCat.copy(alpha = 0.55f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { filtroCategoria = cat }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = textoBoton,
                                color = if (esActivo) colorCat else Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                fontWeight = if (esActivo) FontWeight.ExtraBold else FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            item {
                // Selector de pestañas para filtrar por aciertos y fallos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabsPrincipales = listOf(
                        FiltroResultados.TODAS to totalCategoria,
                        FiltroResultados.ACERTADAS to acertadasCategoria,
                        FiltroResultados.FALLADAS to falladasCategoria
                    )
                    tabsPrincipales.forEach { (filtro, cantidad) ->
                        val esSeleccionado = filtroSeleccionado == filtro ||
                                (filtro == FiltroResultados.FALLADAS && (filtroSeleccionado == FiltroResultados.FALLO_BANDERA || filtroSeleccionado == FiltroResultados.FALLO_CAPITAL))
                        val bg = if (esSeleccionado) colorTema else Color.Transparent
                        val textCol = if (esSeleccionado) {
                            if (tipoQuiz == TipoQuiz.MIXTO) Color.White else DarkBackground
                        } else TextSecondary

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

                // En el Test Mixto, permitir filtrar por tipo de fallo con estructura idéntica
                if (tipoQuiz == TipoQuiz.MIXTO) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCard)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val tabsFallos = listOf(
                            FiltroResultados.FALLADAS to "Todos los fallos ($falladasCategoria)",
                            FiltroResultados.FALLO_BANDERA to "F. Bandera ($fallosBanderaCategoria)",
                            FiltroResultados.FALLO_CAPITAL to "F. Capital ($fallosCapitalCategoria)"
                        )
                        tabsFallos.forEach { (filtro, texto) ->
                            val esSeleccionado = filtroSeleccionado == filtro
                            val bg = if (esSeleccionado) WrongRed else Color.Transparent
                            val textCol = if (esSeleccionado) Color.White else TextSecondary

                            Button(
                                onClick = { filtroSeleccionado = filtro },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = bg,
                                    contentColor = textCol
                                ),
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 2.dp)
                            ) {
                                Text(
                                    text = texto,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (esSeleccionado) FontWeight.ExtraBold else FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
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
                                    FiltroResultados.FALLO_BANDERA -> "¡Excelente! No cometiste ningún fallo en bandera."
                                    FiltroResultados.FALLO_CAPITAL -> "¡Excelente! No cometiste ningún fallo en capital."
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
    categoriaSeleccionada: FiltroCategoriaResultados = FiltroCategoriaResultados.GLOBAL,
    total: Int,
    acertadas: Int,
    falladas: Int,
    fallosBandera: Int = 0,
    fallosCapital: Int = 0,
    porcentaje: Int
) {
    val shape = RoundedCornerShape(20.dp)
    val tituloModo = if (region == RegionQuiz.GLOBAL) {
        val base = when (tipoQuiz) {
            TipoQuiz.BANDERAS -> "Test de banderas"
            TipoQuiz.CAPITALES -> "Test de capitales"
            TipoQuiz.MIXTO -> "Test mixto"
        }
        if (categoriaSeleccionada != FiltroCategoriaResultados.GLOBAL) {
            "$base • ${categoriaSeleccionada.label}"
        } else {
            base
        }
    } else {
        val base = when (tipoQuiz) {
            TipoQuiz.BANDERAS -> "Test de banderas"
            TipoQuiz.CAPITALES -> "Test de capitales"
            TipoQuiz.MIXTO -> "Test mixto"
        }
        if (categoriaSeleccionada != FiltroCategoriaResultados.GLOBAL) {
            "$base • ${region.nombre} (${categoriaSeleccionada.label})"
        } else {
            "$base • ${region.nombre}"
        }
    }

    val colorTemaCard = if (categoriaSeleccionada != FiltroCategoriaResultados.GLOBAL) {
        colorCategoriaResultados(categoriaSeleccionada, region)
    } else if (region != RegionQuiz.GLOBAL) {
        colorRegion(region)
    } else {
        when (tipoQuiz) {
            TipoQuiz.BANDERAS -> PrimaryBlue
            TipoQuiz.CAPITALES -> AccentGold
            TipoQuiz.MIXTO -> Color(0xFFEF4444)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape)
            .border(1.5.dp, colorTemaCard.copy(alpha = 0.4f), shape),
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
                color = colorTemaCard,
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

            // Dos bloques principales: Acertadas y Falladas
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

            // Desglose de fallos en Test Mixto con estructura simétrica
            if (tipoQuiz == TipoQuiz.MIXTO) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Fallos de bandera
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$fallosBandera",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Fallos de bandera",
                                color = TextSecondary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Fallos de capital
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$fallosCapital",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Fallos de capital",
                                color = TextSecondary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
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

                    TipoQuiz.MIXTO -> {
                        if (respuesta.esCorrecta) {
                            Column(modifier = Modifier.padding(top = 2.dp)) {
                                Text(
                                    text = "¡Bandera y capital acertadas!",
                                    color = CorrectGreen,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Capital: ${paisCorrecto.capital}",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        } else {
                            Column(modifier = Modifier.padding(top = 2.dp)) {
                                if (respuesta.falloEnBandera) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    ) {
                                        Text(
                                            text = "Fallo bandera: ",
                                            color = WrongRed,
                                            fontSize = 11.sp
                                        )
                                        BanderaImage(
                                            codigo = respuesta.opcionSeleccionada.codigo,
                                            modifier = Modifier.size(width = 20.dp, height = 13.dp),
                                            borderWidth = 0.dp,
                                            elevation = 0.dp
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "(${respuesta.opcionSeleccionada.nombre})",
                                            color = TextMuted,
                                            fontSize = 10.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                } else if (respuesta.seHaRendido) {
                                    Text(
                                        text = "¡Te has rendido en la capital!",
                                        color = WrongRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                } else if (!respuesta.capitalEscrita.isNullOrBlank()) {
                                    Text(
                                        text = "Escribiste: \"${respuesta.capitalEscrita}\"",
                                        color = WrongRed,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "Capital: ${paisCorrecto.capital}",
                                    color = CorrectGreen,
                                    fontSize = 11.sp,
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
