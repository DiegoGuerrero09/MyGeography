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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.rememberTextMeasurer
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
                // - Mixto: 4 filas x 2 columnas (8 opciones) + Input capital + Feedback
                when (uiState.tipoQuiz) {
                    TipoQuiz.BANDERAS -> {
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
                    }
                    TipoQuiz.CAPITALES -> {
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
                    TipoQuiz.MIXTO -> {
                        ContenidoQuizMixto(
                            pregunta = pregunta,
                            uiState = uiState,
                            onSeleccionarBandera = { viewModel.seleccionarBanderaMixto(it) },
                            onActualizarCapital = { viewModel.actualizarTextoCapitalMixto(it) },
                            onComprobarCapital = { viewModel.comprobarCapitalMixto() },
                            onRendirse = { viewModel.rendirseMixto() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }

                // Barra de navegación inferior: Anterior / Siguiente (o Finalizar)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Anterior: deshabilitado en la primera pregunta
                    OutlinedButton(
                        onClick = { viewModel.retrocederPregunta() },
                        enabled = uiState.puedeRetroceder,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            disabledContentColor = TextMuted
                        ),
                        border = BorderStroke(
                            1.2.dp,
                            if (uiState.puedeRetroceder) PrimaryBlue.copy(alpha = 0.8f) else DarkCardBorder
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Anterior",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Botón Siguiente / Finalizar: no deja ir hacia adelante sin contestar (solo activo al volver atrás)
                    Button(
                        onClick = { viewModel.avanzarSiguientePregunta() },
                        enabled = uiState.puedeAvanzarManualmente,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.esUltimaPregunta) CorrectGreen else PrimaryBlue,
                            disabledContainerColor = Color(0xFF1E293B),
                            contentColor = Color.White,
                            disabledContentColor = TextMuted
                        )
                    ) {
                        Text(
                            text = if (uiState.esUltimaPregunta) "Finalizar" else "Siguiente",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (uiState.esUltimaPregunta) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
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
        when (tipoQuiz) {
            TipoQuiz.BANDERAS -> "Test de banderas"
            TipoQuiz.CAPITALES -> "Test de capitales"
            TipoQuiz.MIXTO -> "Test mixto"
        }
    } else {
        val base = when (tipoQuiz) {
            TipoQuiz.BANDERAS -> "Banderas"
            TipoQuiz.CAPITALES -> "Capitales"
            TipoQuiz.MIXTO -> "Mixto"
        }
        "$base • ${region.nombre}"
    }

    val colorModo = when (tipoQuiz) {
        TipoQuiz.BANDERAS -> PrimaryBlue
        TipoQuiz.CAPITALES -> AccentGold
        TipoQuiz.MIXTO -> Color(0xFFEF4444)
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
                    color = colorModo,
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
            color = colorModo,
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

            TipoQuiz.MIXTO -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¿QUÉ BANDERA Y CAPITAL ES?",
                        color = Color(0xFFEF4444),
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
 * Componente que ajusta dinámicamente el tamaño de fuente midiendo el espacio real disponible
 * para garantizar que textos largos (como "Islas Ultramarinas Menores de los Estados Unidos")
 * quepan siempre enteros en una sola línea sin desbordar ni cortarse.
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
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val maxWidthPx = constraints.maxWidth
        val textMeasurer = rememberTextMeasurer()

        var currentSizeSp by remember(texto, maxWidthPx, baseSize, fontWeight) {
            val initialSize = if (maxWidthPx <= 0) {
                baseSize.toFloat()
            } else {
                val targetWidth = (maxWidthPx - 4).coerceAtLeast(1)
                var size = baseSize.toFloat()
                while (size > 5f) {
                    val result = textMeasurer.measure(
                        text = AnnotatedString(texto),
                        style = TextStyle(
                            fontSize = size.sp,
                            fontWeight = fontWeight
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
            text = texto,
            modifier = Modifier.fillMaxWidth(),
            color = color,
            fontSize = currentSizeSp.sp,
            fontWeight = fontWeight,
            textAlign = textAlign,
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
 * Cuadrícula 4 filas x 2 columnas para el Test Mixto (8 opciones de banderas parecidas).
 */
@Composable
private fun GridBanderas4x2(
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
        verticalArrangement = Arrangement.spacedBy(6.dp)
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

/**
 * Contenedor para el Test Mixto:
 * 1. Cuadrícula 4x2 de banderas parecidas arriba.
 * 2. Caja de texto para la capital abajo (se activa solo al acertar la bandera).
 * 3. Botón de rendirse y botón de comprobar/siguiente.
 */
@Composable
private fun ContenidoQuizMixto(
    pregunta: QuizPregunta,
    uiState: QuizUiState,
    onSeleccionarBandera: (Pais) -> Unit,
    onActualizarCapital: (String) -> Unit,
    onComprobarCapital: () -> Unit,
    onRendirse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val estaHabilitadoCampo = uiState.banderaEsCorrectaMixto == true && !uiState.estaContestada

    // Cuando la bandera es correcta y la pregunta no está contestada, enfocar automáticamente el campo
    LaunchedEffect(uiState.banderaEsCorrectaMixto) {
        if (uiState.banderaEsCorrectaMixto == true && !uiState.estaContestada) {
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {}
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Cuadrícula 4 filas x 2 columnas de banderas (8 opciones)
        GridBanderas4x2(
            opciones = pregunta.opciones,
            paisCorrecto = pregunta.paisCorrecto,
            opcionSeleccionada = uiState.banderaSeleccionadaMixto ?: uiState.opcionSeleccionada,
            estaEvaluando = uiState.banderaSeleccionadaMixto != null || uiState.estaContestada,
            onSeleccionar = onSeleccionarBandera,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Apartado inferior para escribir la capital
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Mensaje de feedback / estado
                if (uiState.feedbackMensajeMixto != null) {
                    val esAcierto = uiState.respuestaActual?.esCorrecta == true
                    val bgFeedback = if (esAcierto) CorrectGreenBg else WrongRedBg
                    val colorFeedback = if (esAcierto) CorrectGreen else WrongRed

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgFeedback)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (esAcierto) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = colorFeedback,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = uiState.feedbackMensajeMixto,
                            color = colorFeedback,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Caja de texto para ingresar el nombre de la capital
                OutlinedTextField(
                    value = uiState.textoCapitalMixto,
                    onValueChange = onActualizarCapital,
                    enabled = estaHabilitadoCampo,
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = if (uiState.banderaEsCorrectaMixto == true) {
                                "Escribe la capital..."
                            } else {
                                "1º Elige la bandera arriba"
                            },
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    isError = uiState.errorCapitalMixto,
                    supportingText = if (uiState.errorCapitalMixto) {
                        {
                            Text(
                                text = "Capital incorrecta. Inténtalo de nuevo o pulsa Rendirse.",
                                color = WrongRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else null,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = if (estaHabilitadoCampo) AccentGold else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onComprobarCapital()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentGold,
                        unfocusedBorderColor = DarkCardBorder,
                        disabledBorderColor = DarkCardBorder.copy(alpha = 0.5f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        disabledTextColor = TextSecondary,
                        errorBorderColor = WrongRed,
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        disabledContainerColor = Color(0xFF0F172A).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                // Botones Rendirse y Comprobar (solo visibles mientras se escribe la capital)
                if (estaHabilitadoCampo) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRendirse,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = WrongRed
                            ),
                            border = BorderStroke(1.dp, WrongRed.copy(alpha = 0.7f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Rendirse",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                onComprobarCapital()
                            },
                            enabled = uiState.textoCapitalMixto.isNotBlank(),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEF4444),
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFF1E293B),
                                disabledContentColor = TextMuted
                            )
                        ) {
                            Text(
                                text = "Comprobar",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

