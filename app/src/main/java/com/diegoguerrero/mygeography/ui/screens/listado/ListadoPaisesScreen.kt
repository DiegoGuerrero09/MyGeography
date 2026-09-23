package com.diegoguerrero.mygeography.ui.screens.listado

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegoguerrero.mygeography.data.datasource.CoordenadasPaises
import com.diegoguerrero.mygeography.data.datasource.PaisesData
import com.diegoguerrero.mygeography.data.model.Continente
import com.diegoguerrero.mygeography.data.model.Pais
import com.diegoguerrero.mygeography.ui.components.BanderaImage
import com.diegoguerrero.mygeography.ui.theme.*
import java.text.Collator
import java.util.Locale
import kotlin.math.abs

private enum class CategoriaFiltro(val label: String) {
    TODOS("Todos (254)"),
    INDEPENDIENTES("Independientes (195)"),
    DEPENDIENTES("Dependientes (59)"),
    AFRICA("África"),
    ANTARTIDA("Antártida"),
    ASIA("Asia"),
    CENTROAMERICA("Centroamérica"),
    EUROPA("Europa"),
    NORTEAMERICA("Norteamérica"),
    OCEANIA("Oceanía"),
    SUDAMERICA("Sudamérica")
}

private val codigosSudamerica = setOf(
    "ar", "bo", "br", "cl", "co", "ec", "fk", "gf", "gy", "pe", "py", "sr", "uy", "ve"
)

private val codigosNorteamerica = setOf(
    "ca", "us", "mx", "bm", "gl", "pm"
)

private val codigosAntartida = setOf(
    "aq", "bv", "gs", "hm", "tf"
)

data class RegionBadgeInfo(val nombre: String, val color: Color)

fun obtenerRegionBadgeInfo(pais: Pais): RegionBadgeInfo {
    return when {
        pais.continente == Continente.EUROPA -> RegionBadgeInfo("Europa", Color(0xFF38BDF8)) // Celeste
        pais.continente == Continente.AFRICA -> RegionBadgeInfo("África", Color(0xFFD97706)) // Ámbar
        pais.continente == Continente.ASIA -> RegionBadgeInfo("Asia", Color(0xFFEF4444))     // Rojo
        pais.continente == Continente.OCEANIA -> RegionBadgeInfo("Oceanía", Color(0xFF10B981)) // Esmeralda
        pais.codigo in codigosAntartida || pais.continente == Continente.ANTARTIDA -> RegionBadgeInfo("Antártida", Color(0xFF94A3B8)) // Hielo
        pais.codigo in codigosSudamerica -> RegionBadgeInfo("Sudamérica", Color(0xFFA855F7)) // Púrpura
        pais.codigo in codigosNorteamerica -> RegionBadgeInfo("Norteamérica", Color(0xFF06B6D4)) // Cian
        pais.continente == Continente.AMERICA -> RegionBadgeInfo("Centroamérica", Color(0xFF84CC16)) // Verde lima tropical
        else -> RegionBadgeInfo(pais.continente.nombre, Color(0xFF64748B))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoPaisesScreen(
    onVolverAlMenu: () -> Unit
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf(CategoriaFiltro.TODOS) }
    var paisSeleccionadoParaModal by remember { mutableStateOf<Pais?>(null) }
    var mostrarDialogoEditarCapital by remember { mutableStateOf(false) }
    var textoNuevaCapital by remember { mutableStateOf("") }
    var errorCapital by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        PaisesData.inicializar(context)
    }

    val todosLosPaises by PaisesData.paisesFlow.collectAsState()
    val collatorEspanol = remember {
        Collator.getInstance(Locale("es", "ES")).apply {
            strength = Collator.SECONDARY
        }
    }

    val paisesFiltrados = remember(textoBusqueda, filtroSeleccionado, todosLosPaises) {
        todosLosPaises.filter { pais ->
            val coincideFiltro = when (filtroSeleccionado) {
                CategoriaFiltro.TODOS -> true
                CategoriaFiltro.INDEPENDIENTES -> pais.esSoberano
                CategoriaFiltro.DEPENDIENTES -> !pais.esSoberano
                CategoriaFiltro.EUROPA -> pais.continente == Continente.EUROPA
                CategoriaFiltro.NORTEAMERICA -> pais.codigo in codigosNorteamerica
                CategoriaFiltro.CENTROAMERICA -> pais.continente == Continente.AMERICA && pais.codigo !in codigosNorteamerica && pais.codigo !in codigosSudamerica
                CategoriaFiltro.SUDAMERICA -> pais.codigo in codigosSudamerica
                CategoriaFiltro.ASIA -> pais.continente == Continente.ASIA
                CategoriaFiltro.AFRICA -> pais.continente == Continente.AFRICA
                CategoriaFiltro.OCEANIA -> pais.continente == Continente.OCEANIA
                CategoriaFiltro.ANTARTIDA -> pais.codigo in codigosAntartida || pais.continente == Continente.ANTARTIDA
            }

            val coincideBusqueda = if (textoBusqueda.isBlank()) {
                true
            } else {
                pais.nombre.contains(textoBusqueda, ignoreCase = true) ||
                        pais.capital.contains(textoBusqueda, ignoreCase = true) ||
                        pais.codigo.contains(textoBusqueda, ignoreCase = true)
            }

            coincideFiltro && coincideBusqueda
        }.sortedWith(compareBy(collatorEspanol) { it.nombre })
    }

    // Modal inferior con detalle completo del país y botón a Wikipedia al pulsar en la lista
    if (paisSeleccionadoParaModal != null) {
        val pais = paisSeleccionadoParaModal!!
        val regionInfo = remember(pais) { obtenerRegionBadgeInfo(pais) }
        val coords = remember(pais) { CoordenadasPaises.obtener(pais.codigo) }
        val wikiUrl = remember(pais.nombre) {
            "https://es.wikipedia.org/wiki/${Uri.encode(pais.nombre.replace(" ", "_"))}"
        }

        ModalBottomSheet(
            onDismissRequest = { paisSeleccionadoParaModal = null },
            containerColor = DarkCard,
            dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BanderaImage(
                        codigo = pais.codigo,
                        modifier = Modifier.size(width = 80.dp, height = 54.dp),
                        borderWidth = 0.dp,
                        elevation = 2.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        NombrePaisModalAutoAjustable(
                            nombre = pais.nombre,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CapitalModalAutoAjustable(
                                capital = pais.capital,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            FilledTonalIconButton(
                                onClick = {
                                    textoNuevaCapital = pais.capital
                                    errorCapital = null
                                    mostrarDialogoEditarCapital = true
                                },
                                modifier = Modifier.size(28.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = Color(0xFF1E293B),
                                    contentColor = PrimaryCyan
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar capital manualmente",
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Estado de dependencia si es dependiente
                if (!pais.esSoberano && !pais.estadoSoberano.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF332005))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "País dependiente • Pertenece a: ${pais.estadoSoberano}",
                            color = Color(0xFFFBBF24),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Badges y Coordenadas con 4 decimales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(regionInfo.color.copy(alpha = 0.18f))
                                .border(1.dp, regionInfo.color.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = regionInfo.nombre,
                                color = regionInfo.color,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (pais.esSoberano) SovereignBadgeBg else TerritoryBadgeBg)
                                .border(
                                    1.dp,
                                    if (pais.esSoberano) SovereignBadgeBorder else TerritoryBadgeBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (pais.esSoberano) "Independiente" else "Dependiente",
                                color = if (pais.esSoberano) SovereignBadgeText else TerritoryBadgeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (coords != null) {
                        val latStr = String.format(Locale.US, "%.4f°%s", abs(coords.first), if (coords.first >= 0) "N" else "S")
                        val lonStr = String.format(Locale.US, "%.4f°%s", abs(coords.second), if (coords.second >= 0) "E" else "O")
                        Text(
                            text = "$latStr, $lonStr",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Botones de acción: Editar Capital y Wikipedia
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            textoNuevaCapital = pais.capital
                            errorCapital = null
                            mostrarDialogoEditarCapital = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF0F172A),
                            contentColor = PrimaryCyan
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Editar capital",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikiUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Wikipedia",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Diálogo para editar la capital manualmente
    if (mostrarDialogoEditarCapital && paisSeleccionadoParaModal != null) {
        val paisActual = paisSeleccionadoParaModal!!
        val capitalOriginal = remember(paisActual.codigo) {
            PaisesData.obtenerCapitalOriginal(paisActual.codigo) ?: paisActual.capital
        }
        val esPersonalizada = remember(paisActual.codigo, todosLosPaises) {
            PaisesData.esCapitalPersonalizada(paisActual.codigo)
        }

        AlertDialog(
            onDismissRequest = { mostrarDialogoEditarCapital = false },
            containerColor = DarkCard,
            shape = RoundedCornerShape(20.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PrimaryCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Editar capital",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = paisActual.nombre,
                        color = PrimaryCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = textoNuevaCapital,
                        onValueChange = {
                            textoNuevaCapital = it
                            if (errorCapital != null && it.isNotBlank()) {
                                errorCapital = null
                            }
                        },
                        label = { Text("Capital") },
                        placeholder = { Text("Escribe la capital...") },
                        singleLine = true,
                        isError = errorCapital != null,
                        supportingText = {
                            if (errorCapital != null) {
                                Text(
                                    text = errorCapital!!,
                                    color = Color(0xFFEF4444),
                                    fontSize = 12.sp
                                )
                            } else if (esPersonalizada) {
                                Text(
                                    text = "Capital predeterminada: $capitalOriginal",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        trailingIcon = {
                            if (textoNuevaCapital.isNotEmpty()) {
                                IconButton(onClick = { textoNuevaCapital = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Limpiar",
                                        tint = TextMuted
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkBackground,
                            unfocusedContainerColor = DarkBackground,
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = PrimaryCyan,
                            unfocusedLabelColor = TextMuted
                        )
                    )

                    if (esPersonalizada || textoNuevaCapital.trim() != capitalOriginal) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(
                            onClick = {
                                textoNuevaCapital = capitalOriginal
                                errorCapital = null
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Restablecer capital original",
                                color = PrimaryCyan,
                                fontSize = 12.5.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val nueva = textoNuevaCapital.trim()
                        if (nueva.isBlank()) {
                            errorCapital = "La capital no puede estar vacía"
                            return@Button
                        }
                        val paisActualizado = if (nueva == capitalOriginal) {
                            PaisesData.restablecerCapital(paisActual.codigo, context)
                        } else {
                            PaisesData.actualizarCapital(paisActual.codigo, nueva, context)
                        }
                        if (paisActualizado != null) {
                            paisSeleccionadoParaModal = paisActualizado
                        }
                        mostrarDialogoEditarCapital = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoEditarCapital = false }
                ) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBarListado(
                totalResultados = paisesFiltrados.size,
                onVolver = onVolverAlMenu
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Buscador
            BarraBuscador(
                texto = textoBusqueda,
                onTextoCambiado = { textoBusqueda = it },
                onLimpiar = { textoBusqueda = "" }
            )

            // Filtros horizontales
            FilaFiltros(
                filtroSeleccionado = filtroSeleccionado,
                onSeleccionarFiltro = { filtroSeleccionado = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de Resultados
            if (paisesFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No se encontraron resultados",
                            color = TextSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Prueba con otro término o filtro",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = paisesFiltrados,
                        key = { it.codigo }
                    ) { pais ->
                        ItemPaisCard(
                            pais = pais,
                            onClick = { paisSeleccionadoParaModal = pais }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarListado(
    totalResultados: Int,
    onVolver: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Listado de países",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.offset(y = 3.dp)
                )
                Text(
                    text = "$totalResultados naciones encontradas",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onVolver) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkCard
        )
    )
}

@Composable
private fun BarraBuscador(
    texto: String,
    onTextoCambiado: (String) -> Unit,
    onLimpiar: () -> Unit
) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoCambiado,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = {
            Text(
                text = "Buscar país, capital o código...",
                color = TextMuted,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = TextMuted
            )
        },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = onLimpiar) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        tint = TextMuted
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkCard,
            unfocusedContainerColor = DarkCard,
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = DarkCardBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        )
    )
}

@Composable
private fun FilaFiltros(
    filtroSeleccionado: CategoriaFiltro,
    onSeleccionarFiltro: (CategoriaFiltro) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(CategoriaFiltro.values()) { filtro ->
            val esActivo = filtro == filtroSeleccionado
            FilterChip(
                selected = esActivo,
                onClick = { onSeleccionarFiltro(filtro) },
                label = {
                    Text(
                        text = filtro.label,
                        fontSize = 12.sp,
                        fontWeight = if (esActivo) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = DarkCard,
                    labelColor = TextSecondary,
                    selectedContainerColor = PrimaryBlue,
                    selectedLabelColor = DarkBackground
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (esActivo) PrimaryBlue else DarkCardBorder,
                    selectedBorderColor = PrimaryBlue,
                    enabled = true,
                    selected = esActivo
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
private fun ItemPaisCard(
    pais: Pais,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    val regionInfo = remember(pais) { obtenerRegionBadgeInfo(pais) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, DarkCardBorder, shape)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bandera en tamaño amplio sin reborde (para que banderas no estándar no queden raras)
            BanderaImage(
                codigo = pais.codigo,
                modifier = Modifier
                    .size(width = 68.dp, height = 45.dp),
                borderWidth = 0.dp,
                elevation = 0.dp
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Nombre, Capital y Soberanía si es territorio
            Column(modifier = Modifier.weight(1f)) {
                NombrePaisAutoAjustable(
                    nombre = pais.nombre,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                CapitalAutoAjustable(
                    capital = pais.capital,
                    modifier = Modifier.fillMaxWidth()
                )

                if (!pais.esSoberano && !pais.estadoSoberano.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    DependienteDeAutoAjustable(
                        estadoSoberano = pais.estadoSoberano,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Badges de Independencia y Continente
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Badge de Independiente / Dependiente
                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (pais.esSoberano) SovereignBadgeBg else TerritoryBadgeBg)
                        .border(
                            width = 1.dp,
                            color = if (pais.esSoberano) SovereignBadgeBorder else TerritoryBadgeBorder,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (pais.esSoberano) "Independiente" else "Dependiente",
                        color = if (pais.esSoberano) SovereignBadgeText else TerritoryBadgeText,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }

                // Badge de Continente
                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(regionInfo.color.copy(alpha = 0.18f))
                        .border(
                            width = 1.dp,
                            color = regionInfo.color.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = regionInfo.nombre,
                        color = regionInfo.color,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Componente que mide el espacio horizontal real disponible y reduce dinámicamente la fuente
 * para que los nombres de países quepan completos en una sola línea sin cortarse jamás.
 */
@Composable
private fun NombrePaisAutoAjustable(
    nombre: String,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val maxWidthPx = constraints.maxWidth
        val textMeasurer = rememberTextMeasurer()

        var currentSizeSp by remember(nombre, maxWidthPx) {
            val initialSize = if (maxWidthPx <= 0) {
                15f
            } else {
                val targetWidth = (maxWidthPx - 8).coerceAtLeast(1)
                var size = 15f
                while (size > 5f) {
                    val result = textMeasurer.measure(
                        text = AnnotatedString(nombre),
                        style = TextStyle(
                            fontSize = size.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                    if (result.size.width <= targetWidth) {
                        break
                    }
                    size -= 0.35f
                }
                size
            }
            mutableFloatStateOf(initialSize)
        }

        Text(
            text = nombre,
            color = TextPrimary,
            fontSize = currentSizeSp.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            onTextLayout = { layoutResult ->
                if (layoutResult.hasVisualOverflow && currentSizeSp > 5f) {
                    currentSizeSp -= 0.5f
                }
            }
        )
    }
}

/**
 * Componente que mide el espacio disponible para la capital y reduce dinámicamente la fuente
 * utilizando un único Text con AnnotatedString para evitar saltos o recortes de texto.
 * Garantiza que capitales largas como "Sri Jayawardenepura Kotte / Colombo" quepan completas sin cortarse.
 */
@Composable
private fun CapitalAutoAjustable(
    capital: String,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val maxWidthPx = constraints.maxWidth
        val textMeasurer = rememberTextMeasurer()
        val prefix = "Capital: "

        val fullAnnotated = remember(capital) {
            buildAnnotatedString {
                withStyle(SpanStyle(color = TextMuted, fontWeight = FontWeight.Normal)) {
                    append(prefix)
                }
                withStyle(SpanStyle(color = PrimaryCyan, fontWeight = FontWeight.SemiBold)) {
                    append(capital)
                }
            }
        }

        var currentSizeSp by remember(capital, maxWidthPx) {
            val initialSize = if (maxWidthPx <= 0) {
                12f
            } else {
                val targetWidth = (maxWidthPx - 8).coerceAtLeast(1)
                var size = 12f
                while (size > 5f) {
                    val result = textMeasurer.measure(
                        text = fullAnnotated,
                        style = TextStyle(
                            fontSize = size.sp
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                    if (result.size.width <= targetWidth) {
                        break
                    }
                    size -= 0.35f
                }
                size
            }
            mutableFloatStateOf(initialSize)
        }

        Text(
            text = fullAnnotated,
            fontSize = currentSizeSp.sp,
            maxLines = 1,
            softWrap = false,
            onTextLayout = { layoutResult ->
                if (layoutResult.hasVisualOverflow && currentSizeSp > 5f) {
                    currentSizeSp -= 0.5f
                }
            }
        )
    }
}

/**
 * Componente para el nombre del país en el modal de detalle, adaptando el tamaño de fuente
 * dinámicamente para que quepa siempre en una sola línea completa sin cortarse.
 */
@Composable
private fun NombrePaisModalAutoAjustable(
    nombre: String,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val maxWidthPx = constraints.maxWidth
        val textMeasurer = rememberTextMeasurer()

        var currentSizeSp by remember(nombre, maxWidthPx) {
            val initialSize = if (maxWidthPx <= 0) {
                20f
            } else {
                val targetWidth = (maxWidthPx - 4).coerceAtLeast(1)
                var size = 20f
                while (size > 7f) {
                    val result = textMeasurer.measure(
                        text = AnnotatedString(nombre),
                        style = TextStyle(
                            fontSize = size.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                    if (result.size.width <= targetWidth) {
                        break
                    }
                    size -= 0.35f
                }
                size
            }
            mutableFloatStateOf(initialSize)
        }

        Text(
            text = nombre,
            color = Color.White,
            fontSize = currentSizeSp.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            onTextLayout = { layoutResult ->
                if (layoutResult.hasVisualOverflow && currentSizeSp > 7f) {
                    currentSizeSp -= 0.5f
                }
            }
        )
    }
}

/**
 * Componente para la capital del país en el modal de detalle, adaptando el tamaño de fuente
 * dinámicamente para que quepa siempre en una sola línea completa sin saltos ni recortes.
 */
@Composable
private fun CapitalModalAutoAjustable(
    capital: String,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val maxWidthPx = constraints.maxWidth
        val textMeasurer = rememberTextMeasurer()
        val prefix = "Capital: "

        val fullAnnotated = remember(capital) {
            buildAnnotatedString {
                withStyle(SpanStyle(color = TextMuted, fontWeight = FontWeight.Normal)) {
                    append(prefix)
                }
                withStyle(SpanStyle(color = PrimaryCyan, fontWeight = FontWeight.SemiBold)) {
                    append(capital)
                }
            }
        }

        var currentSizeSp by remember(capital, maxWidthPx) {
            val initialSize = if (maxWidthPx <= 0) {
                14f
            } else {
                val targetWidth = (maxWidthPx - 4).coerceAtLeast(1)
                var size = 14f
                while (size > 6f) {
                    val result = textMeasurer.measure(
                        text = fullAnnotated,
                        style = TextStyle(
                            fontSize = size.sp
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                    if (result.size.width <= targetWidth) {
                        break
                    }
                    size -= 0.3f
                }
                size
            }
            mutableFloatStateOf(initialSize)
        }

        Text(
            text = fullAnnotated,
            fontSize = currentSizeSp.sp,
            maxLines = 1,
            softWrap = false,
            onTextLayout = { layoutResult ->
                if (layoutResult.hasVisualOverflow && currentSizeSp > 6f) {
                    currentSizeSp -= 0.5f
                }
            }
        )
    }
}

/**
 * Componente que mide el espacio disponible y reduce dinámicamente el tamaño de la fuente
 * para que el texto "Dependiente de: ..." quepa en una sola línea completa sin cortarse.
 */
@Composable
private fun DependienteDeAutoAjustable(
    estadoSoberano: String,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val maxWidthPx = constraints.maxWidth
        val textMeasurer = rememberTextMeasurer()
        val textoCompleto = "Dependiente de: $estadoSoberano"

        var currentSizeSp by remember(textoCompleto, maxWidthPx) {
            val initialSize = if (maxWidthPx <= 0) {
                10.5f
            } else {
                val targetWidth = (maxWidthPx - 4).coerceAtLeast(1)
                var size = 10.5f
                while (size > 5f) {
                    val result = textMeasurer.measure(
                        text = AnnotatedString(textoCompleto),
                        style = TextStyle(
                            fontSize = size.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                    if (result.size.width <= targetWidth) {
                        break
                    }
                    size -= 0.35f
                }
                size
            }
            mutableFloatStateOf(initialSize)
        }

        Text(
            text = textoCompleto,
            color = Color(0xFFFBBF24),
            fontSize = currentSizeSp.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false,
            onTextLayout = { layoutResult ->
                if (layoutResult.hasVisualOverflow && currentSizeSp > 5f) {
                    currentSizeSp -= 0.5f
                }
            }
        )
    }
}

