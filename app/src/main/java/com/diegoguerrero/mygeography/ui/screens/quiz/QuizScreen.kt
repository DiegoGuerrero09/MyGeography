package com.diegoguerrero.mygeography.ui.screens.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegoguerrero.mygeography.data.model.Pais
import com.diegoguerrero.mygeography.data.model.QuizPregunta
import com.diegoguerrero.mygeography.data.model.RegionQuiz
import com.diegoguerrero.mygeography.data.model.TipoQuiz
import com.diegoguerrero.mygeography.ui.components.BanderaImage
import com.diegoguerrero.mygeography.ui.theme.*

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onVolverAlMenu: () -> Unit,
    onQuizTerminado: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Manejador del botón 'Atrás' del sistema
    BackHandler {
        viewModel.setMostrarDialogoSalir(true)
    }

    // Navegar a resultados cuando finalice el quiz
    LaunchedEffect(uiState.quizTerminado) {
        if (uiState.quizTerminado) {
            onQuizTerminado()
        }
    }

    // Diálogo de confirmación para salir al menú
    if (uiState.mostrarDialogoSalir) {
        AlertDialog(
            onDismissRequest = { viewModel.setMostrarDialogoSalir(false) },
            containerColor = DarkCard,
            title = {
                Text(
                    text = "¿Deseas salir del test?",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Si sales ahora perderás tu progreso actual.",
                    color = TextSecondary,
                    fontSize = 12.5.sp,
                    maxLines = 1,
                    softWrap = false
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setMostrarDialogoSalir(false)
                        onVolverAlMenu()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WrongRed)
                ) {
                    Text(text = "Salir al menú", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.setMostrarDialogoSalir(false) },
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(DarkCardBorder)
                    )
                ) {
                    Text(text = "Continuar", color = TextPrimary)
                }
            }
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            QuizTopBar(
                tipoQuiz = uiState.tipoQuiz,
                region = uiState.region,
                indiceActual = uiState.indiceActual,
                totalPreguntas = uiState.totalPreguntas,
                progreso = uiState.progreso,
                aciertos = uiState.aciertos,
                fallos = uiState.fallos,
                onSolicitarSalir = { viewModel.setMostrarDialogoSalir(true) }
            )
        }
    ) { innerPadding ->
        uiState.preguntaActual?.let { pregunta ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cabecera con la pregunta actual
                CabeceraPregunta(
                    tipoQuiz = uiState.tipoQuiz,
                    pregunta = pregunta,
                    modifier = Modifier.fillMaxWidth()
                )

                // Distribución de opciones que ocupa todo el espacio libre de la pantalla:
                // - Banderas: 6 filas x 2 columnas (12 opciones)
                // - Capitales: 12 filas x 1 columna (12 opciones)
                if (uiState.tipoQuiz == TipoQuiz.BANDERAS) {
                    GridBanderas6x2(
                        opciones = pregunta.opciones,
                        paisCorrecto = pregunta.paisCorrecto,
                        opcionSeleccionada = uiState.opcionSeleccionada,
                        estaEvaluando = uiState.estaEvaluando,
                        onSeleccionar = { viewModel.seleccionarOpcion(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                } else {
                    ListaCapitales12x1(
                        opciones = pregunta.opciones,
                        paisCorrecto = pregunta.paisCorrecto,
                        opcionSeleccionada = uiState.opcionSeleccionada,
                        estaEvaluando = uiState.estaEvaluando,
                        onSeleccionar = { viewModel.seleccionarOpcion(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
private fun QuizTopBar(
    tipoQuiz: TipoQuiz,
    region: RegionQuiz,
    indiceActual: Int,
    totalPreguntas: Int,
    progreso: Float,
    aciertos: Int,
    fallos: Int,
    onSolicitarSalir: () -> Unit
) {
    val tituloModo = if (region == RegionQuiz.GLOBAL) {
        if (tipoQuiz == TipoQuiz.BANDERAS) "Test de banderas" else "Test de capitales"
    } else {
        val base = if (tipoQuiz == TipoQuiz.BANDERAS) "Banderas" else "Capitales"
        "$base • ${region.nombre}"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón Salir al menú
            IconButton(
                onClick = onSolicitarSalir,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DarkCard)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver al menú",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Indicador de Pregunta actual y modo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).padding(horizontal = 6.dp)
            ) {
                Text(
                    text = tituloModo,
                    color = PrimaryBlue,
                    fontSize = if (tituloModo.length > 25) 10.5.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${indiceActual + 1} de $totalPreguntas",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Marcadores de aciertos y fallos
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Aciertos
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CorrectGreenBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Aciertos",
                        tint = CorrectGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$aciertos",
                        color = CorrectGreen,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Fallos
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(WrongRedBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fallos",
                        tint = WrongRed,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$fallos",
                        color = WrongRed,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Barra de progreso continua
        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.5.dp),
            color = PrimaryBlue,
            trackColor = DarkCardBorder
        )
    }
}

@Composable
private fun CabeceraPregunta(
    tipoQuiz: TipoQuiz,
    pregunta: QuizPregunta,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(14.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        when (tipoQuiz) {
            TipoQuiz.BANDERAS -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¿QUÉ BANDERA ES?",
                        color = PrimaryBlue,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    TextoAjustable(
                        texto = pregunta.paisCorrecto.nombre,
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        baseSize = 20
                    )

                    Spacer(modifier = Modifier.height(1.dp))

                    Text(
                        text = pregunta.paisCorrecto.continente.nombre,
                        color = TextMuted,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            TipoQuiz.CAPITALES -> {
                // Cabecera dividida: Lado izquierdo FIJO para bandera, lado derecho para textos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bandera fija en tamaño y posición sin reborde
                    Box(
                        modifier = Modifier
                            .width(88.dp)
                            .height(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BanderaImage(
                            codigo = pregunta.paisCorrecto.codigo,
                            modifier = Modifier.fillMaxSize(),
                            borderWidth = 0.dp,
                            elevation = 0.dp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Lado derecho: Textos con tamaño ajustable para no mover la bandera
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "CAPITAL DE",
                            color = AccentGold,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        TextoAjustable(
                            texto = pregunta.paisCorrecto.nombre,
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Start,
                            baseSize = 18
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = pregunta.paisCorrecto.continente.nombre,
                            color = TextSecondary,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Cuadrícula 6 filas x 2 columnas para el Test de Banderas (12 opciones).
 * Se expande proporcionalmente para ocupar el espacio restante de la pantalla.
 */
@Composable
private fun GridBanderas6x2(
    opciones: List<Pais>,
    paisCorrecto: Pais,
    opcionSeleccionada: Pais?,
    estaEvaluando: Boolean,
    onSeleccionar: (Pais) -> Unit,
    modifier: Modifier = Modifier
) {
    val filas = opciones.chunked(2)

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        for (fila in filas) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (opcion in fila) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        OpcionBanderaCard(
                            opcion = opcion,
                            paisCorrecto = paisCorrecto,
                            opcionSeleccionada = opcionSeleccionada,
                            estaEvaluando = estaEvaluando,
                            onSeleccionar = { onSeleccionar(opcion) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OpcionBanderaCard(
    opcion: Pais,
    paisCorrecto: Pais,
    opcionSeleccionada: Pais?,
    estaEvaluando: Boolean,
    onSeleccionar: () -> Unit
) {
    val esEstaSeleccionada = opcion.codigo == opcionSeleccionada?.codigo
    val esEstaCorrecta = opcion.codigo == paisCorrecto.codigo

    val targetBorderColor = when {
        estaEvaluando && esEstaCorrecta -> CorrectGreenBorder
        estaEvaluando && esEstaSeleccionada && !esEstaCorrecta -> WrongRedBorder
        else -> DarkCardBorder
    }

    val targetBgColor = when {
        estaEvaluando && esEstaCorrecta -> CorrectGreenBg
        estaEvaluando && esEstaSeleccionada && !esEstaCorrecta -> WrongRedBg
        else -> DarkCard
    }

    val borderColor by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = tween(250),
        label = "borderColor"
    )

    val bgColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(250),
        label = "bgColor"
    )

    val shape = RoundedCornerShape(10.dp)
    val borderWidth = if (estaEvaluando && (esEstaCorrecta || esEstaSeleccionada)) 2.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape)
            .border(borderWidth, borderColor, shape)
            .clickable(enabled = !estaEvaluando) { onSeleccionar() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            BanderaImage(
                codigo = opcion.codigo,
                modifier = Modifier.fillMaxSize(),
                borderWidth = 0.dp,
                elevation = 0.dp
            )
        }
    }
}

/**
 * Lista 12 filas x 1 columna para el Test de Capitales (12 opciones).
 * Se expande proporcionalmente para ocupar el espacio restante de la pantalla.
 */
@Composable
private fun ListaCapitales12x1(
    opciones: List<Pais>,
    paisCorrecto: Pais,
    opcionSeleccionada: Pais?,
    estaEvaluando: Boolean,
    onSeleccionar: (Pais) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (opcion in opciones) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                OpcionCapitalCard(
                    opcion = opcion,
                    paisCorrecto = paisCorrecto,
                    opcionSeleccionada = opcionSeleccionada,
                    estaEvaluando = estaEvaluando,
                    onSeleccionar = { onSeleccionar(opcion) }
                )
            }
        }
    }
}

@Composable
private fun OpcionCapitalCard(
    opcion: Pais,
    paisCorrecto: Pais,
    opcionSeleccionada: Pais?,
    estaEvaluando: Boolean,
    onSeleccionar: () -> Unit
) {
    val esEstaSeleccionada = opcion.codigo == opcionSeleccionada?.codigo
    val esEstaCorrecta = opcion.codigo == paisCorrecto.codigo

    val targetBorderColor = when {
        estaEvaluando && esEstaCorrecta -> CorrectGreenBorder
        estaEvaluando && esEstaSeleccionada && !esEstaCorrecta -> WrongRedBorder
        else -> DarkCardBorder
    }

    val targetBgColor = when {
        estaEvaluando && esEstaCorrecta -> CorrectGreenBg
        estaEvaluando && esEstaSeleccionada && !esEstaCorrecta -> WrongRedBg
        else -> DarkCard
    }

    val borderColor by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = tween(250),
        label = "borderColor"
    )

    val bgColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(250),
        label = "bgColor"
    )

    val shape = RoundedCornerShape(8.dp)
    val borderWidth = if (estaEvaluando && (esEstaCorrecta || esEstaSeleccionada)) 2.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape)
            .border(borderWidth, borderColor, shape)
            .clickable(enabled = !estaEvaluando) { onSeleccionar() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            TextoAjustable(
                texto = opcion.capital,
                color = when {
                    estaEvaluando && esEstaCorrecta -> CorrectGreen
                    estaEvaluando && esEstaSeleccionada && !esEstaCorrecta -> WrongRed
                    else -> TextPrimary
                },
                fontWeight = if (estaEvaluando && (esEstaCorrecta || esEstaSeleccionada))
                    FontWeight.ExtraBold else FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                baseSize = 14
            )
        }
    }
}

/**
 * Componente que ajusta dinámicamente el tamaño de fuente si el texto es muy largo
 * para garantizar que siempre se mantenga en una sola línea sin desbordar ni desplazar componentes.
 */
@Composable
fun TextoAjustable(
    texto: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign = TextAlign.Center,
    baseSize: Int = 18
) {
    val fontSize = when {
        texto.length > 28 -> (baseSize - 6).coerceAtLeast(9).sp
        texto.length > 22 -> (baseSize - 4).coerceAtLeast(10).sp
        texto.length > 16 -> (baseSize - 2).coerceAtLeast(11).sp
        else -> baseSize.sp
    }

    Text(
        text = texto,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = 1,
        softWrap = false
    )
}
