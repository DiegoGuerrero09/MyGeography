package com.diegoguerrero.mygeography.ui.screens.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegoguerrero.mygeography.data.model.Pais
import com.diegoguerrero.mygeography.data.model.QuizPregunta
import com.diegoguerrero.mygeography.data.model.TipoQuiz
import com.diegoguerrero.mygeography.ui.components.BanderaImage
import com.diegoguerrero.mygeography.ui.theme.*

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onVolverAlMenu: () -> Unit,
    onFinalizarQuiz: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Manejar retroceso físico / botón back del dispositivo
    BackHandler {
        viewModel.setMostrarDialogoSalir(true)
    }

    LaunchedEffect(uiState.quizTerminado) {
        if (uiState.quizTerminado) {
            onFinalizarQuiz()
        }
    }

    val preguntaActual = uiState.preguntaActual

    if (uiState.mostrarDialogoSalir) {
        AlertDialog(
            onDismissRequest = { viewModel.setMostrarDialogoSalir(false) },
            containerColor = DarkCard,
            title = {
                Text(
                    text = "Abandonar test",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Deseas volver al menú principal? Se perderá el progreso de esta partida.",
                    color = TextSecondary,
                    fontSize = 14.sp
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
                    Text("Volver al menú", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setMostrarDialogoSalir(false) }) {
                    Text("Continuar test", color = PrimaryBlue)
                }
            }
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            QuizTopBar(
                tipoQuiz = uiState.tipoQuiz,
                indiceActual = uiState.indiceActual,
                totalPreguntas = uiState.totalPreguntas,
                progreso = uiState.progreso,
                aciertos = uiState.aciertos,
                fallos = uiState.fallos,
                onSolicitarSalir = { viewModel.setMostrarDialogoSalir(true) }
            )
        }
    ) { innerPadding ->
        if (preguntaActual != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Zona superior: Pregunta
                CabeceraPregunta(
                    tipoQuiz = uiState.tipoQuiz,
                    pregunta = preguntaActual,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Zona Central: Grid 3x3 de Opciones (9 opciones)
                Grid3x3Opciones(
                    tipoQuiz = uiState.tipoQuiz,
                    opciones = preguntaActual.opciones,
                    paisCorrecto = preguntaActual.paisCorrecto,
                    opcionSeleccionada = uiState.opcionSeleccionada,
                    estaEvaluando = uiState.estaEvaluando,
                    onSeleccionar = { opcion -> viewModel.seleccionarOpcion(opcion) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Botón visual para avanzar de inmediato si el usuario no desea esperar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.estaEvaluando) {
                        Button(
                            onClick = { viewModel.avanzarSiguientePregunta() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            Text(
                                text = "Siguiente",
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizTopBar(
    tipoQuiz: TipoQuiz,
    indiceActual: Int,
    totalPreguntas: Int,
    progreso: Float,
    aciertos: Int,
    fallos: Int,
    onSolicitarSalir: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón Abandonar / Salir
            IconButton(
                onClick = onSolicitarSalir,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DarkCard)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver al menú",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Indicador de Pregunta actual
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = tipoQuiz.titulo,
                    color = PrimaryBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${indiceActual + 1} de $totalPreguntas",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Marcadores de aciertos y fallos
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$aciertos",
                        color = CorrectGreen,
                        fontSize = 13.sp,
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
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$fallos",
                        color = WrongRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Barra de progreso animada
        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
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
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (tipoQuiz) {
                TipoQuiz.BANDERAS -> {
                    Text(
                        text = "¿QUÉ BANDERA ES?",
                        color = PrimaryBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = pregunta.paisCorrecto.nombre,
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = pregunta.paisCorrecto.continente.nombre,
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                TipoQuiz.CAPITALES -> {
                    // Muestra país y su bandera arriba
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BanderaImage(
                            codigo = pregunta.paisCorrecto.codigo,
                            modifier = Modifier
                                .size(width = 96.dp, height = 64.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            elevation = 4.dp
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "¿CAPITAL DE:",
                                color = AccentGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = pregunta.paisCorrecto.nombre,
                                color = TextPrimary,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = pregunta.paisCorrecto.continente.nombre,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Grid3x3Opciones(
    tipoQuiz: TipoQuiz,
    opciones: List<Pais>,
    paisCorrecto: Pais,
    opcionSeleccionada: Pais?,
    estaEvaluando: Boolean,
    onSeleccionar: (Pais) -> Unit,
    modifier: Modifier = Modifier
) {
    // Organiza las 9 opciones en 3 filas de 3 columnas
    val filas = opciones.chunked(3)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (fila in filas) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (opcion in fila) {
                    Box(modifier = Modifier.weight(1f)) {
                        OpcionCard(
                            tipoQuiz = tipoQuiz,
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
private fun OpcionCard(
    tipoQuiz: TipoQuiz,
    opcion: Pais,
    paisCorrecto: Pais,
    opcionSeleccionada: Pais?,
    estaEvaluando: Boolean,
    onSeleccionar: () -> Unit
) {
    val esEstaSeleccionada = opcion.codigo == opcionSeleccionada?.codigo
    val esEstaCorrecta = opcion.codigo == paisCorrecto.codigo

    // Determinación de colores según la respuesta
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

    val shape = RoundedCornerShape(12.dp)
    val borderWidth = if (estaEvaluando && (esEstaCorrecta || esEstaSeleccionada)) 2.5.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(if (tipoQuiz == TipoQuiz.BANDERAS) 1.25f else 1.15f)
            .shadow(3.dp, shape)
            .clip(shape)
            .border(borderWidth, borderColor, shape)
            .clickable(enabled = !estaEvaluando) { onSeleccionar() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = shape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (tipoQuiz) {
                TipoQuiz.BANDERAS -> {
                    BanderaImage(
                        codigo = opcion.codigo,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        cornerRadius = 8.dp,
                        borderWidth = 0.dp
                    )
                }

                TipoQuiz.CAPITALES -> {
                    Text(
                        text = opcion.capital,
                        color = if (estaEvaluando && esEstaCorrecta) CorrectGreen
                                else if (estaEvaluando && esEstaSeleccionada && !esEstaCorrecta) WrongRed
                                else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(6.dp),
                        lineHeight = 16.sp
                    )
                }
            }

            // Indicador de resultado (Checkmark o Cruz) si fue seleccionada o es la correcta
            if (estaEvaluando) {
                if (esEstaCorrecta) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(CorrectGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Correcta",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                } else if (esEstaSeleccionada) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(WrongRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Incorrecta",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}
