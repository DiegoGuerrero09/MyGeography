package com.diegoguerrero.mygeography.ui.screens.listado

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegoguerrero.mygeography.data.datasource.PaisesData
import com.diegoguerrero.mygeography.data.model.Continente
import com.diegoguerrero.mygeography.data.model.Pais
import com.diegoguerrero.mygeography.ui.components.BanderaImage
import com.diegoguerrero.mygeography.ui.theme.*

private enum class CategoriaFiltro(val label: String) {
    TODOS("Todos (254)"),
    SOBERANOS("Soberanos (195)"),
    EUROPA("Europa"),
    AMERICA("América"),
    ASIA("Asia"),
    AFRICA("África"),
    OCEANIA("Oceanía")
}

@Composable
fun ListadoPaisesScreen(
    onVolverAlMenu: () -> Unit
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf(CategoriaFiltro.TODOS) }

    val todosLosPaises = remember { PaisesData.listaPaises }

    val paisesFiltrados = remember(textoBusqueda, filtroSeleccionado) {
        todosLosPaises.filter { pais ->
            val coincideFiltro = when (filtroSeleccionado) {
                CategoriaFiltro.TODOS -> true
                CategoriaFiltro.SOBERANOS -> pais.esSoberano
                CategoriaFiltro.EUROPA -> pais.continente == Continente.EUROPA
                CategoriaFiltro.AMERICA -> pais.continente == Continente.AMERICA
                CategoriaFiltro.ASIA -> pais.continente == Continente.ASIA
                CategoriaFiltro.AFRICA -> pais.continente == Continente.AFRICA
                CategoriaFiltro.OCEANIA -> pais.continente == Continente.OCEANIA
            }

            val coincideBusqueda = if (textoBusqueda.isBlank()) {
                true
            } else {
                pais.nombre.contains(textoBusqueda, ignoreCase = true) ||
                        pais.capital.contains(textoBusqueda, ignoreCase = true) ||
                        pais.codigo.contains(textoBusqueda, ignoreCase = true)
            }

            coincideFiltro && coincideBusqueda
        }
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

            // Lista de Países
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(paisesFiltrados, key = { it.codigo }) { pais ->
                    ItemPaisCard(pais = pais)
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun TopAppBarListado(
    totalResultados: Int,
    onVolver: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onVolver,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DarkCard)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Listado de Países",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$totalResultados naciones encontradas",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
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
            Text("Buscar por país o capital...", color = TextMuted, fontSize = 14.sp)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = onLimpiar) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
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
private fun ItemPaisCard(pais: Pais) {
    val shape = RoundedCornerShape(14.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkCardBorder, shape),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bandera en alta calidad
            BanderaImage(
                codigo = pais.codigo,
                modifier = Modifier
                    .size(width = 66.dp, height = 44.dp)
                    .clip(RoundedCornerShape(6.dp)),
                elevation = 2.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Nombre y Capital
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pais.nombre,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Capital: ",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = pais.capital,
                        color = PrimaryCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Badges de Soberanía y Continente
            Column(horizontalAlignment = Alignment.End) {
                // Badge de Soberano / Territorio
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (pais.esSoberano) SovereignBadgeBg else TerritoryBadgeBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (pais.esSoberano) "Soberano" else "Territorio",
                        color = if (pais.esSoberano) SovereignBadgeText else TerritoryBadgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = pais.continente.nombre,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}
