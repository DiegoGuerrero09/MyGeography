package com.diegoguerrero.mygeography.ui.screens.globo

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegoguerrero.mygeography.data.datasource.CoordenadasPaises
import com.diegoguerrero.mygeography.data.datasource.PaisesData
import com.diegoguerrero.mygeography.data.datasource.TierrasData
import com.diegoguerrero.mygeography.data.model.Continente
import com.diegoguerrero.mygeography.data.model.Pais
import com.diegoguerrero.mygeography.ui.components.BanderaImage
import com.diegoguerrero.mygeography.ui.screens.listado.obtenerRegionBadgeInfo
import com.diegoguerrero.mygeography.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.*

private data class PaisProyectado(
    val pais: Pais,
    val lat: Float,
    val lon: Float,
    val x: Float,
    val y: Float,
    val cosC: Float,
    val visible: Boolean
)

/**
 * Pantalla de Globo Terráqueo 3D interactivo.
 * Permite rotar en 360°, hacer zoom in/out con gestos o botones,
 * visualizar continentes, graticules y nombres de países claros en la vista previa,
 * y seleccionar cualquier país para consultar su bandera y capital.
 */
@Composable
fun GloboTerraqueoScreen(
    onVolverAlMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rotacionLat by remember { mutableFloatStateOf(20f) }
    var rotacionLon by remember { mutableFloatStateOf(0f) }
    var zoomFactor by remember { mutableFloatStateOf(1.0f) }
    var autoGirar by remember { mutableStateOf(false) }
    var mostrarNombresPaises by remember { mutableStateOf(true) }

    var paisSeleccionado by remember { mutableStateOf<Pais?>(null) }
    var queryBusqueda by remember { mutableStateOf("") }
    var busquedaActiva by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Auto-rotación continua
    LaunchedEffect(autoGirar) {
        while (autoGirar) {
            withFrameMillis {
                rotacionLon = (rotacionLon + 0.18f) % 360f
            }
        }
    }

    val todosPaises = remember { PaisesData.listaPaises }
    val paisesFiltrados = remember(queryBusqueda) {
        if (queryBusqueda.isBlank()) emptyList()
        else {
            val q = queryBusqueda.trim().lowercase()
            todosPaises.filter {
                it.nombre.lowercase().contains(q) ||
                it.capital.lowercase().contains(q) ||
                it.codigo.lowercase() == q
            }.take(6)
        }
    }

    // Función para centrar y enfocar un país suavemente sin condiciones de carrera
    val centrarEnPais: (Pais) -> Unit = { pais ->
        val coords = CoordenadasPaises.obtener(pais.codigo)
        if (coords != null) {
            autoGirar = false
            paisSeleccionado = pais
            val targetLat = coords.first.coerceIn(-80f, 80f)
            val targetLon = coords.second

            // Calcular ruta angular más corta para la longitud
            var diffLon = (targetLon - rotacionLon) % 360f
            if (diffLon > 180f) diffLon -= 360f
            if (diffLon < -180f) diffLon += 360f
            val endLon = rotacionLon + diffLon

            val startLat = rotacionLat
            val startLon = rotacionLon
            val startZoom = zoomFactor
            val targetZoom = if (zoomFactor < 2.0f) 2.2f else zoomFactor

            coroutineScope.launch {
                animate(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = tween(650, easing = FastOutSlowInEasing)
                ) { progress, _ ->
                    rotacionLat = startLat + (targetLat - startLat) * progress
                    rotacionLon = (startLon + (endLon - startLon) * progress) % 360f
                    zoomFactor = startZoom + (targetZoom - startZoom) * progress
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712)) // Espacio profundo
    ) {
        // Lienzo del Globo Terráqueo Interactivo
        LienzoGlobo(
            rotacionLat = rotacionLat,
            rotacionLon = rotacionLon,
            zoomFactor = zoomFactor,
            mostrarNombresPaises = mostrarNombresPaises,
            paisSeleccionado = paisSeleccionado,
            onRotar = { dLat, dLon ->
                autoGirar = false
                // Controles naturales no invertidos: la superficie sigue el dedo
                rotacionLat = (rotacionLat + dLat * 0.35f).coerceIn(-85f, 85f)
                rotacionLon = (rotacionLon - dLon * 0.35f) % 360f
            },
            onZoomChange = { scale ->
                // Permitir zoom ampliado hasta 8.5x y alejar hasta 0.5x
                zoomFactor = (zoomFactor * scale).coerceIn(0.5f, 8.5f)
            },
            onSeleccionarPais = { pais ->
                autoGirar = false
                paisSeleccionado = pais
            },
            modifier = Modifier.fillMaxSize()
        )

        // Barra superior con botón volver, título y barra de búsqueda
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 14.dp, end = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón volver
                IconButton(
                    onClick = onVolverAlMenu,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkCard.copy(alpha = 0.85f))
                        .border(1.dp, DarkCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Título central
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Globo terráqueo",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Botón alternar búsqueda
                IconButton(
                    onClick = { busquedaActiva = !busquedaActiva },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (busquedaActiva) PrimaryBlue else DarkCard.copy(alpha = 0.85f))
                        .border(1.dp, if (busquedaActiva) PrimaryCyan else DarkCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (busquedaActiva) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Desplegable de búsqueda de países
            AnimatedVisibility(
                visible = busquedaActiva,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    TextField(
                        value = queryBusqueda,
                        onValueChange = { queryBusqueda = it },
                        placeholder = {
                            Text(
                                "Buscar país o capital...",
                                color = TextMuted,
                                fontSize = 13.5.sp
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkCard,
                            unfocusedContainerColor = DarkCard,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = PrimaryCyan,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        trailingIcon = {
                            if (queryBusqueda.isNotEmpty()) {
                                IconButton(onClick = { queryBusqueda = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Limpiar",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, PrimaryCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    )

                    if (paisesFiltrados.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                                items(paisesFiltrados) { pais ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                centrarEnPais(pais)
                                                busquedaActiva = false
                                                queryBusqueda = ""
                                            }
                                            .padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BanderaImage(
                                            codigo = pais.codigo,
                                            modifier = Modifier.size(width = 34.dp, height = 23.dp),
                                            borderWidth = 0.dp,
                                            elevation = 0.dp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = pais.nombre,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "Capital: ${pais.capital}",
                                                color = AccentGold,
                                                fontSize = 11.5.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Botones flotantes de control de zoom y rotación a la derecha
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Botón Zoom In
            BotonControlGlobo(
                icono = Icons.Default.Add,
                descripcion = "Acercar",
                onClick = { zoomFactor = (zoomFactor * 1.25f).coerceAtMost(8.5f) }
            )

            // Botón Zoom Out
            BotonControlGlobo(
                icono = Icons.Default.Remove,
                descripcion = "Alejar",
                onClick = { zoomFactor = (zoomFactor / 1.25f).coerceAtLeast(0.5f) }
            )

            // Botón Centrar / Reset
            BotonControlGlobo(
                icono = Icons.Default.FilterCenterFocus,
                descripcion = "Restablecer vista",
                onClick = {
                    autoGirar = false
                    val startLat = rotacionLat
                    val startLon = rotacionLon
                    var diffLon = (0f - startLon) % 360f
                    if (diffLon > 180f) diffLon -= 360f
                    if (diffLon < -180f) diffLon += 360f
                    val endLon = startLon + diffLon
                    val startZoom = zoomFactor

                    coroutineScope.launch {
                        animate(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = tween(600, easing = FastOutSlowInEasing)
                        ) { progress, _ ->
                            rotacionLat = startLat + (20f - startLat) * progress
                            rotacionLon = (startLon + (endLon - startLon) * progress) % 360f
                            zoomFactor = startZoom + (1.0f - startZoom) * progress
                        }
                    }
                }
            )

            // Botón Mostrar/Ocultar nombres de países
            BotonControlGlobo(
                icono = if (mostrarNombresPaises) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                descripcion = if (mostrarNombresPaises) "Ocultar nombres" else "Mostrar nombres",
                colorActivo = if (mostrarNombresPaises) PrimaryCyan else TextMuted,
                onClick = { mostrarNombresPaises = !mostrarNombresPaises }
            )

            // Botón Auto-rotación
            BotonControlGlobo(
                icono = if (autoGirar) Icons.Default.Pause else Icons.Default.PlayArrow,
                descripcion = "Auto-giro",
                colorActivo = if (autoGirar) PrimaryCyan else Color.White,
                onClick = { autoGirar = !autoGirar }
            )
        }

        // Tarjeta flotante inferior con los datos del país seleccionado (Bandera, Capital, Continente)
        AnimatedVisibility(
            visible = paisSeleccionado != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            paisSeleccionado?.let { pais ->
                CardDetallePaisGlobo(
                    pais = pais,
                    onCentrar = { centrarEnPais(pais) },
                    onCerrar = { paisSeleccionado = null }
                )
            }
        }
    }
}

/**
 * Botón circular para controles de zoom y navegación del globo
 */
@Composable
private fun BotonControlGlobo(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    descripcion: String,
    onClick: () -> Unit,
    colorActivo: Color = Color.White
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(DarkCard.copy(alpha = 0.9f))
            .border(1.2.dp, DarkCardBorder, CircleShape)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = descripcion,
            tint = colorActivo,
            modifier = Modifier.size(22.dp)
        )
    }
}

/**
 * Tarjeta de detalles del país seleccionado en el globo terráqueo
 */
@Composable
private fun CardDetallePaisGlobo(
    pais: Pais,
    onCentrar: () -> Unit,
    onCerrar: () -> Unit
) {
    val context = LocalContext.current
    val regionInfo = remember(pais) { obtenerRegionBadgeInfo(pais) }
    val coords = remember(pais) { CoordenadasPaises.obtener(pais.codigo) }
    val shape = RoundedCornerShape(18.dp)
    val wikiUrl = remember(pais) {
        val slug = pais.nombre.replace(" ", "_")
        "https://es.wikipedia.org/wiki/$slug"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.2.dp, PrimaryCyan.copy(alpha = 0.5f), shape),
        colors = CardDefaults.cardColors(containerColor = DarkCard.copy(alpha = 0.96f)),
        shape = shape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Bandera en tamaño amplio sin reborde
                    BanderaImage(
                        codigo = pais.codigo,
                        modifier = Modifier.size(width = 68.dp, height = 45.dp),
                        borderWidth = 0.dp,
                        elevation = 2.dp
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = pais.nombre,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Capital: ",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                            Text(
                                text = pais.capital,
                                color = AccentGold,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onCerrar,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Indicación clara si es un país dependiente
            if (!pais.esSoberano && !pais.estadoSoberano.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF332005))
                        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "País dependiente • Pertenece a: ${pais.estadoSoberano}",
                        color = Color(0xFFFBBF24),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fila de Badges, Coordenadas a 4 decimales y botones (Wikipedia encima de Centrar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Badge Continente / Región
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(regionInfo.color.copy(alpha = 0.18f))
                                .border(1.dp, regionInfo.color.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = regionInfo.nombre,
                                color = regionInfo.color,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Badge Independiente / Dependiente
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (pais.esSoberano) SovereignBadgeBg else TerritoryBadgeBg)
                                .border(
                                    1.dp,
                                    if (pais.esSoberano) SovereignBadgeBorder else TerritoryBadgeBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (pais.esSoberano) "Independiente" else "Dependiente",
                                color = if (pais.esSoberano) SovereignBadgeText else TerritoryBadgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Coordenadas con 4 decimales
                    if (coords != null) {
                        val latStr = String.format(Locale.US, "%.4f°%s", abs(coords.first), if (coords.first >= 0) "N" else "S")
                        val lonStr = String.format(Locale.US, "%.4f°%s", abs(coords.second), if (coords.second >= 0) "E" else "O")
                        Text(
                            text = "$latStr, $lonStr",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Columna de acciones: Botón Wikipedia encima de Centrar
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Botón Wikipedia encima del botón de centrar
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikiUrl))
                            context.startActivity(intent)
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Wikipedia",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Botón Centrar
                    OutlinedButton(
                        onClick = onCentrar,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryCyan),
                        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.7f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterCenterFocus,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Centrar",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Lienzo Canvas donde se renderiza la proyección ortográfica esférica 3D:
 * Océano azul, continentes e islas en verde, fronteras de países, graticule,
 * puntos coloreados por continente (territorios en gris) y etiquetas de nombres.
 */
@Composable
private fun LienzoGlobo(
    rotacionLat: Float,
    rotacionLon: Float,
    zoomFactor: Float,
    mostrarNombresPaises: Boolean,
    paisSeleccionado: Pais?,
    onRotar: (Float, Float) -> Unit,
    onZoomChange: (Float) -> Unit,
    onSeleccionarPais: (Pais) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    // Caché de países, masas de tierra y fronteras políticas
    val listaPaises = remember { PaisesData.listaPaises }
    val masasTierra = remember { TierrasData.masasDeTierra }
    val fronterasPaises = remember { TierrasData.fronterasPaises }

    // Generar estrellas fijas de fondo
    val estrellas = remember {
        List(60) {
            Pair(
                (0..1000).random() / 1000f,
                (0..1000).random() / 1000f
            )
        }
    }

    var proyectadosPorFrame by remember { mutableStateOf<List<PaisProyectado>>(emptyList()) }

    val currentOnZoomChange by rememberUpdatedState(onZoomChange)
    val currentOnRotar by rememberUpdatedState(onRotar)
    val currentOnSeleccionarPais by rememberUpdatedState(onSeleccionarPais)

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // Zoom con gestos (pellizco con dos dedos para acercar o alejar)
                    if (zoom != 1f) {
                        val factor = (1f + (zoom - 1f) * 1.75f).coerceIn(0.5f, 2.0f)
                        currentOnZoomChange(factor)
                    }

                    // Durante pellizco activo de zoom (zoom != 1f), amortiguar fuertemente
                    // el desplazamiento del centroide para no rotar violentamente el globo
                    if (abs(zoom - 1f) < 0.005f) {
                        // Rotación natural de arrastre
                        if (pan != Offset.Zero) {
                            currentOnRotar(pan.y, pan.x)
                        }
                    } else {
                        // Movimiento residual durante zoom con dos dedos
                        if (pan != Offset.Zero) {
                            currentOnRotar(pan.y * 0.1f, pan.x * 0.1f)
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        // Doble toque para hacer zoom in fluido con gesto
                        currentOnZoomChange(1.4f)
                    },
                    onTap = { tapOffset ->
                        // Buscar el país visible más cercano al toque
                        val cercano = proyectadosPorFrame
                            .filter { it.visible }
                            .map { p ->
                                val dist = hypot(p.x - tapOffset.x, p.y - tapOffset.y)
                                Pair(p, dist)
                            }
                            .filter { it.second < 45.dp.toPx() }
                            .minByOrNull { it.second }

                        if (cercano != null) {
                            currentOnSeleccionarPais(cercano.first.pais)
                        }
                    }
                )
            }
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val cx = width / 2f
            val cy = height / 2f

            // Radio base del globo
            val radioBase = min(width, height) * 0.42f
            val R = radioBase * zoomFactor

            // 1. Dibujar estrellas de fondo
            estrellas.forEach { (sx, sy) ->
                val starX = sx * width
                val starY = sy * height
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f),
                    radius = 1.2.dp.toPx(),
                    center = Offset(starX, starY)
                )
            }

            // 2. Halo exterior de atmósfera celeste
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x3338BDF8),
                        Color(0x180284C7),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = R * 1.12f
                ),
                radius = R * 1.12f,
                center = Offset(cx, cy)
            )

            // 3. Océano base de la Tierra (esfera con sombreado 3D de agua azul)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF132B45),
                        Color(0xFF0C1D30),
                        Color(0xFF07121E)
                    ),
                    center = Offset(cx - R * 0.25f, cy - R * 0.25f),
                    radius = R * 1.35f
                ),
                radius = R,
                center = Offset(cx, cy)
            )

            // Ángulos de cámara en radianes
            val phi0 = Math.toRadians(rotacionLat.toDouble()).toFloat()
            val lambda0 = Math.toRadians(rotacionLon.toDouble()).toFloat()
            val cosPhi0 = cos(phi0)
            val sinPhi0 = sin(phi0)

            // Máscara de recorte circular para que nada sobresalga del globo
            val clipGlobe = Path().apply {
                addOval(Rect(cx - R, cy - R, cx + R, cy + R))
            }

            clipPath(clipGlobe) {
                // 4. Dibujar Graticule (Paralelos y Meridianos)
                // Ecuador y trópicos
                val paralelos = listOf(-60f, -30f, 0f, 30f, 60f)
                paralelos.forEach { latDeg ->
                    val phi = Math.toRadians(latDeg.toDouble()).toFloat()
                    val cosPhi = cos(phi)
                    val sinPhi = sin(phi)
                    val colorParalelo = if (latDeg == 0f) Color(0x5538BDF8) else Color(0x2238BDF8)

                    var prevX = 0f
                    var prevY = 0f
                    var prevVis = false

                    for (lonDeg in -180..180 step 4) {
                        val lambda = Math.toRadians(lonDeg.toDouble()).toFloat()
                        val dLambda = lambda - lambda0
                        val cosC = sinPhi0 * sinPhi + cosPhi0 * cosPhi * cos(dLambda)

                        if (cosC > 0) {
                            val px = cx + R * cosPhi * sin(dLambda)
                            val py = cy - R * (cosPhi0 * sinPhi - sinPhi0 * cosPhi * cos(dLambda))

                            if (prevVis) {
                                drawLine(
                                    color = colorParalelo,
                                    start = Offset(prevX, prevY),
                                    end = Offset(px, py),
                                    strokeWidth = if (latDeg == 0f) 1.2.dp.toPx() else 0.8.dp.toPx()
                                )
                            }
                            prevX = px
                            prevY = py
                            prevVis = true
                        } else {
                            prevVis = false
                        }
                    }
                }

                // Meridianos
                for (lonDeg in -180 until 180 step 45) {
                    val lambda = Math.toRadians(lonDeg.toDouble()).toFloat()
                    val dLambda = lambda - lambda0
                    var prevX = 0f
                    var prevY = 0f
                    var prevVis = false

                    for (latDeg in -80..80 step 4) {
                        val phi = Math.toRadians(latDeg.toDouble()).toFloat()
                        val cosPhi = cos(phi)
                        val sinPhi = sin(phi)
                        val cosC = sinPhi0 * sinPhi + cosPhi0 * cosPhi * cos(dLambda)

                        if (cosC > 0) {
                            val px = cx + R * cosPhi * sin(dLambda)
                            val py = cy - R * (cosPhi0 * sinPhi - sinPhi0 * cosPhi * cos(dLambda))

                            if (prevVis) {
                                drawLine(
                                    color = Color(0x2238BDF8),
                                    start = Offset(prevX, prevY),
                                    end = Offset(px, py),
                                    strokeWidth = 0.8.dp.toPx()
                                )
                            }
                            prevX = px
                            prevY = py
                            prevVis = true
                        } else {
                            prevVis = false
                        }
                    }
                }

                // 5. Dibujar Continentes y Masas de Tierra (tierra verde y agua azul)
                val colorRellenoTierra = Color(0xFF1B3D2B) // Verde bosque / tierra firme
                val colorCostas = Color(0xFF34D399)       // Verde esmeralda menta para costas
                val colorFronteras = Color(0xFFCBD5E1).copy(alpha = 0.45f) // Fronteras internacionales

                masasTierra.forEach { ring ->
                    val len = ring.size
                    if (len >= 6) {
                        // Construir path cerrado para las masas de tierra e islas visibles
                        val pathTierra = Path()
                        var primerPunto = true
                        var puntosVisibles = 0

                        var i = 0
                        while (i < len - 1) {
                            val lat = ring[i]
                            val lon = ring[i + 1]
                            val phi = Math.toRadians(lat.toDouble()).toFloat()
                            val lambda = Math.toRadians(lon.toDouble()).toFloat()
                            val dLambda = lambda - lambda0
                            val cosPhi = cos(phi)
                            val sinPhi = sin(phi)
                            val cosC = sinPhi0 * sinPhi + cosPhi0 * cosPhi * cos(dLambda)

                            if (cosC > 0f) {
                                val px = cx + R * cosPhi * sin(dLambda)
                                val py = cy - R * (cosPhi0 * sinPhi - sinPhi0 * cosPhi * cos(dLambda))
                                if (primerPunto) {
                                    pathTierra.moveTo(px, py)
                                    primerPunto = false
                                } else {
                                    pathTierra.lineTo(px, py)
                                }
                                puntosVisibles++
                            }
                            i += 2
                        }

                        if (puntosVisibles >= 3) {
                            pathTierra.close()
                            drawPath(pathTierra, color = colorRellenoTierra)
                        }
                    }

                    // Trazar línea de costa con gran definición
                    var lastVis = false
                    var lastX = 0f
                    var lastY = 0f

                    val lenRing = ring.size
                    var i = 0
                    while (i < lenRing - 1) {
                        val lat = ring[i]
                        val lon = ring[i + 1]
                        val phi = Math.toRadians(lat.toDouble()).toFloat()
                        val lambda = Math.toRadians(lon.toDouble()).toFloat()
                        val dLambda = lambda - lambda0
                        val cosPhi = cos(phi)
                        val sinPhi = sin(phi)
                        val cosC = sinPhi0 * sinPhi + cosPhi0 * cosPhi * cos(dLambda)

                        if (cosC > -0.05f) {
                            val px = cx + R * cosPhi * sin(dLambda)
                            val py = cy - R * (cosPhi0 * sinPhi - sinPhi0 * cosPhi * cos(dLambda))

                            if (lastVis) {
                                drawLine(
                                    color = colorCostas,
                                    start = Offset(lastX, lastY),
                                    end = Offset(px, py),
                                    strokeWidth = 1.3.dp.toPx()
                                )
                            }
                            lastX = px
                            lastY = py
                            lastVis = true
                        } else {
                            lastVis = false
                        }
                        i += 2
                    }
                }

                // 5.1 Dibujar Fronteras Políticas de Países
                fronterasPaises.forEach { polyline ->
                    val len = polyline.size
                    if (len >= 4) {
                        var lastVis = false
                        var lastX = 0f
                        var lastY = 0f
                        var i = 0
                        while (i < len - 1) {
                            val lat = polyline[i]
                            val lon = polyline[i + 1]
                            val phi = Math.toRadians(lat.toDouble()).toFloat()
                            val lambda = Math.toRadians(lon.toDouble()).toFloat()
                            val dLambda = lambda - lambda0
                            val cosPhi = cos(phi)
                            val sinPhi = sin(phi)
                            val cosC = sinPhi0 * sinPhi + cosPhi0 * cosPhi * cos(dLambda)

                            if (cosC > 0.05f) {
                                val px = cx + R * cosPhi * sin(dLambda)
                                val py = cy - R * (cosPhi0 * sinPhi - sinPhi0 * cosPhi * cos(dLambda))

                                if (lastVis) {
                                    drawLine(
                                        color = colorFronteras,
                                        start = Offset(lastX, lastY),
                                        end = Offset(px, py),
                                        strokeWidth = 0.9.dp.toPx()
                                    )
                                }
                                lastX = px
                                lastY = py
                                lastVis = true
                            } else {
                                lastVis = false
                            }
                            i += 2
                        }
                    }
                }

                // 6. Proyectar países sobre la esfera
                val proyectados = mutableListOf<PaisProyectado>()
                listaPaises.forEach { pais ->
                    val coords = CoordenadasPaises.obtener(pais.codigo)
                    if (coords != null) {
                        val lat = coords.first
                        val lon = coords.second
                        val phi = Math.toRadians(lat.toDouble()).toFloat()
                        val lambda = Math.toRadians(lon.toDouble()).toFloat()
                        val dLambda = lambda - lambda0
                        val cosPhi = cos(phi)
                        val sinPhi = sin(phi)
                        val cosC = sinPhi0 * sinPhi + cosPhi0 * cosPhi * cos(dLambda)

                        val visible = cosC > 0.08f
                        val px = cx + R * cosPhi * sin(dLambda)
                        val py = cy - R * (cosPhi0 * sinPhi - sinPhi0 * cosPhi * cos(dLambda))

                        proyectados.add(
                            PaisProyectado(
                                pais = pais,
                                lat = lat,
                                lon = lon,
                                x = px,
                                y = py,
                                cosC = cosC,
                                visible = visible
                            )
                        )
                    }
                }
                proyectadosPorFrame = proyectados

                // 7. Dibujar marcadores y nombres de países en la vista previa
                // Ordenar por visibilidad (más centrales hacia adelante)
                val visibles = proyectados.filter { it.visible }.sortedBy { it.cosC }

                visibles.forEach { item ->
                    val esSeleccionado = item.pais.codigo == paisSeleccionado?.codigo

                    // Punto luminoso en la ubicación del país coloreado por continente (territorios siempre grises)
                    val radioPunto = if (esSeleccionado) 5.5.dp.toPx() else (2.4.dp.toPx() * (0.8f + item.cosC * 0.4f))
                    val colorPunto = when {
                        esSeleccionado -> Color(0xFFFACC15) // Oro vibrante para el seleccionado
                        !item.pais.esSoberano -> Color(0xFF94A3B8) // Territorios siempre grises
                        item.pais.continente == Continente.EUROPA -> Color(0xFF38BDF8) // Celeste Europa
                        item.pais.continente == Continente.AFRICA -> Color(0xFFF59E0B) // Ámbar cálido África
                        item.pais.continente == Continente.ASIA -> Color(0xFFEF4444)   // Rojo coral Asia
                        item.pais.continente == Continente.OCEANIA -> Color(0xFF10B981) // Verde esmeralda Oceanía
                        item.pais.continente == Continente.AMERICA -> Color(0xFFA855F7) // Púrpura brillante América
                        else -> Color(0xFF93C5FD) // Azul hielo Antártida
                    }

                    if (esSeleccionado) {
                        // Anillo pulsante alrededor del seleccionado
                        drawCircle(
                            color = Color(0x55FACC15),
                            radius = radioPunto * 2.5f,
                            center = Offset(item.x, item.y)
                        )
                        drawCircle(
                            color = Color(0xFFFACC15),
                            radius = radioPunto * 1.8f,
                            center = Offset(item.x, item.y),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }

                    drawCircle(
                        color = colorPunto,
                        radius = radioPunto,
                        center = Offset(item.x, item.y)
                    )

                    // Mostrar etiqueta de nombre clara (respetando el botón de mostrar/ocultar)
                    val debeMostrarTexto = mostrarNombresPaises && (
                        esSeleccionado || (zoomFactor >= 1.6f && item.cosC > 0.35f) || (item.cosC > 0.72f && item.pais.esSoberano)
                    )

                    if (debeMostrarTexto) {
                        val fontSizeSp = if (esSeleccionado) 12.sp else 9.5.sp
                        val layoutResult = textMeasurer.measure(
                            text = AnnotatedString(item.pais.nombre),
                            style = TextStyle(
                                color = Color.White,
                                fontSize = fontSizeSp,
                                fontWeight = if (esSeleccionado) FontWeight.ExtraBold else FontWeight.Bold
                            )
                        )

                        val padX = 5.dp.toPx()
                        val padY = 2.dp.toPx()
                        val boxW = layoutResult.size.width + padX * 2
                        val boxH = layoutResult.size.height + padY * 2
                        val boxLeft = item.x - boxW / 2f
                        val boxTop = item.y - boxH - 6.dp.toPx()

                        // Fondo tipo píldora oscuro con alto contraste
                        drawRoundRect(
                            color = if (esSeleccionado) Color(0xF00F172A) else Color(0xCC090D16),
                            topLeft = Offset(boxLeft, boxTop),
                            size = Size(boxW, boxH),
                            cornerRadius = CornerRadius(5.dp.toPx()),
                            style = Fill
                        )

                        // Borde de la píldora
                        drawRoundRect(
                            color = if (esSeleccionado) Color(0xFFFACC15) else Color(0x5538BDF8),
                            topLeft = Offset(boxLeft, boxTop),
                            size = Size(boxW, boxH),
                            cornerRadius = CornerRadius(5.dp.toPx()),
                            style = Stroke(width = if (esSeleccionado) 1.5.dp.toPx() else 0.8.dp.toPx())
                        )

                        // Texto del nombre del país
                        drawText(
                            textLayoutResult = layoutResult,
                            topLeft = Offset(boxLeft + padX, boxTop + padY)
                        )
                    }
                }
            }

            // 8. Borde fino iluminado en el limbo de la esfera (borde 3D)
            drawCircle(
                color = Color(0x4438BDF8),
                radius = R,
                center = Offset(cx, cy),
                style = Stroke(width = 1.2.dp.toPx())
            )
        }
    }
}
