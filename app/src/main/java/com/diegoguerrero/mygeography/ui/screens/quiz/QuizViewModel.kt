package com.diegoguerrero.mygeography.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoguerrero.mygeography.data.model.Pais
import com.diegoguerrero.mygeography.data.model.QuizPregunta
import com.diegoguerrero.mygeography.data.model.RegionQuiz
import com.diegoguerrero.mygeography.data.model.RespuestaQuiz
import com.diegoguerrero.mygeography.data.model.TipoQuiz
import com.diegoguerrero.mygeography.data.repository.PaisesRepository
import com.diegoguerrero.mygeography.data.util.ValidadorCapital
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val tipoQuiz: TipoQuiz = TipoQuiz.BANDERAS,
    val region: RegionQuiz = RegionQuiz.GLOBAL,
    val incluirDependientes: Boolean = true,
    val preguntas: List<QuizPregunta> = emptyList(),
    val indiceActual: Int = 0,
    val maxIndiceAlcanzado: Int = 0,
    val respuestasPorIndice: Map<Int, RespuestaQuiz> = emptyMap(),
    val quizTerminado: Boolean = false,
    val mostrarDialogoSalir: Boolean = false,
    // Estado específico de Test Mixto:
    val banderaSeleccionadaMixto: Pais? = null,
    val banderaEsCorrectaMixto: Boolean? = null,
    val textoCapitalMixto: String = "",
    val errorCapitalMixto: Boolean = false,
    val feedbackMensajeMixto: String? = null,
    val otrasCapitalesMixto: List<String> = emptyList()
) {
    val preguntaActual: QuizPregunta?
        get() = preguntas.getOrNull(indiceActual)

    val respuestaActual: RespuestaQuiz?
        get() = respuestasPorIndice[indiceActual]

    val estaContestada: Boolean
        get() = respuestaActual != null

    val opcionSeleccionada: Pais?
        get() = respuestaActual?.opcionSeleccionada ?: banderaSeleccionadaMixto

    val esRespuestaCorrecta: Boolean?
        get() = respuestaActual?.esCorrecta ?: banderaEsCorrectaMixto

    val estaEvaluando: Boolean
        get() = estaContestada

    val puedeAvanzar: Boolean
        get() = estaContestada

    // El botón de avanzar manualmente está activo siempre que la pregunta esté contestada
    val puedeAvanzarManualmente: Boolean
        get() = estaContestada

    val puedeRetroceder: Boolean
        get() = indiceActual > 0

    val esUltimaPregunta: Boolean
        get() = totalPreguntas > 0 && indiceActual == totalPreguntas - 1

    val respuestas: List<RespuestaQuiz>
        get() = respuestasPorIndice.values.toList()

    val aciertos: Int
        get() = respuestas.count { it.esCorrecta }

    val fallos: Int
        get() = respuestas.count { !it.esCorrecta }

    val totalPreguntas: Int
        get() = preguntas.size

    val progreso: Float
        get() = if (totalPreguntas > 0) (indiceActual + 1).toFloat() / totalPreguntas.toFloat() else 0f
}

class QuizViewModel(
    private val repository: PaisesRepository = PaisesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var autoAvanzarJob: Job? = null

    fun iniciarQuiz(
        tipo: TipoQuiz,
        region: RegionQuiz = RegionQuiz.GLOBAL,
        incluirDependientes: Boolean = true
    ) {
        autoAvanzarJob?.cancel()
        val preguntas = when (tipo) {
            TipoQuiz.BANDERAS -> repository.generarQuizBanderas(region, incluirDependientes)
            TipoQuiz.CAPITALES -> repository.generarQuizCapitales(region, incluirDependientes)
            TipoQuiz.MIXTO -> repository.generarQuizMixto(region, incluirDependientes)
        }

        _uiState.value = QuizUiState(
            tipoQuiz = tipo,
            region = region,
            incluirDependientes = incluirDependientes,
            preguntas = preguntas,
            indiceActual = 0,
            maxIndiceAlcanzado = 0,
            respuestasPorIndice = emptyMap(),
            quizTerminado = false,
            mostrarDialogoSalir = false,
            banderaSeleccionadaMixto = null,
            banderaEsCorrectaMixto = null,
            textoCapitalMixto = "",
            errorCapitalMixto = false,
            feedbackMensajeMixto = null,
            otrasCapitalesMixto = emptyList()
        )
    }

    fun seleccionarOpcion(opcion: Pais) {
        val currentState = _uiState.value
        // Si ya está contestada la pregunta o terminó el quiz, no permitir cambiar la respuesta
        if (currentState.estaContestada || currentState.quizTerminado) return

        val preguntaActual = currentState.preguntaActual ?: return
        val esCorrecta = opcion.codigo == preguntaActual.paisCorrecto.codigo

        val nuevaRespuesta = RespuestaQuiz(
            pregunta = preguntaActual,
            opcionSeleccionada = opcion,
            esCorrecta = esCorrecta
        )

        _uiState.update {
            it.copy(
                respuestasPorIndice = it.respuestasPorIndice + (it.indiceActual to nuevaRespuesta)
            )
        }

        // Auto-avanzar automáticamente a la siguiente pregunta tras breve pausa para ver la corrección
        autoAvanzarJob?.cancel()
        autoAvanzarJob = viewModelScope.launch {
            delay(1100)
            avanzarSiguientePregunta()
        }
    }

    /**
     * Paso 1 de Test Mixto: El usuario selecciona una bandera.
     * Si falla: cuenta error inmediatamente, muestra la capital y avanza.
     * Si acierta: activa la caja de texto para escribir la capital.
     */
    fun seleccionarBanderaMixto(opcion: Pais) {
        val currentState = _uiState.value
        if (currentState.estaContestada || currentState.quizTerminado) return
        if (currentState.banderaSeleccionadaMixto != null) return

        val preguntaActual = currentState.preguntaActual ?: return
        val esCorrecta = opcion.codigo == preguntaActual.paisCorrecto.codigo

        if (!esCorrecta) {
            val respuesta = RespuestaQuiz(
                pregunta = preguntaActual,
                opcionSeleccionada = opcion,
                esCorrecta = false,
                falloEnBandera = true,
                capitalEscrita = null
            )
            _uiState.update {
                it.copy(
                    banderaSeleccionadaMixto = opcion,
                    banderaEsCorrectaMixto = false,
                    feedbackMensajeMixto = "¡Bandera incorrecta! La capital es: ${preguntaActual.paisCorrecto.capital}",
                    respuestasPorIndice = it.respuestasPorIndice + (it.indiceActual to respuesta)
                )
            }
            autoAvanzarJob?.cancel()
            autoAvanzarJob = viewModelScope.launch {
                delay(1800)
                avanzarSiguientePregunta()
            }
        } else {
            _uiState.update {
                it.copy(
                    banderaSeleccionadaMixto = opcion,
                    banderaEsCorrectaMixto = true,
                    errorCapitalMixto = false,
                    feedbackMensajeMixto = null
                )
            }
        }
    }

    fun actualizarTextoCapitalMixto(texto: String) {
        val currentState = _uiState.value
        if (currentState.estaContestada || currentState.quizTerminado) return
        if (currentState.banderaEsCorrectaMixto != true) return

        val preguntaActual = currentState.preguntaActual ?: return
        val textoEscrito = texto.trim()

        _uiState.update {
            it.copy(
                textoCapitalMixto = texto,
                errorCapitalMixto = false
            )
        }

        // Validación automática: en cuanto lo escrito coincide con una capital válida, pasa automáticamente
        if (textoEscrito.isNotBlank() && ValidadorCapital.esCapitalValida(preguntaActual.paisCorrecto, textoEscrito)) {
            val otras = ValidadorCapital.obtenerOtrasCapitales(preguntaActual.paisCorrecto, textoEscrito)
            val feedback = if (otras.isNotEmpty()) {
                "¡Correcto! Otras capitales: ${otras.joinToString(", ")}"
            } else {
                "¡Correcto!"
            }

            val respuesta = RespuestaQuiz(
                pregunta = preguntaActual,
                opcionSeleccionada = currentState.banderaSeleccionadaMixto ?: preguntaActual.paisCorrecto,
                esCorrecta = true,
                capitalEscrita = textoEscrito
            )

            _uiState.update {
                it.copy(
                    errorCapitalMixto = false,
                    feedbackMensajeMixto = feedback,
                    otrasCapitalesMixto = otras,
                    respuestasPorIndice = it.respuestasPorIndice + (it.indiceActual to respuesta)
                )
            }

            autoAvanzarJob?.cancel()
            autoAvanzarJob = viewModelScope.launch {
                delay(1200)
                avanzarSiguientePregunta()
            }
        }
    }

    /**
     * Paso 2 de Test Mixto: El usuario valida la capital escrita (por ejemplo al pulsar Enter).
     * Si es correcta: cuenta acierto y avanza (mostrando otras capitales si las tiene).
     * Si es incorrecta: muestra error y NO avanza hasta que esté bien o pulse Rendirse.
     */
    fun comprobarCapitalMixto() {
        val currentState = _uiState.value
        if (currentState.estaContestada || currentState.quizTerminado) return
        if (currentState.banderaEsCorrectaMixto != true) return

        val preguntaActual = currentState.preguntaActual ?: return
        val textoEscrito = currentState.textoCapitalMixto.trim()

        if (textoEscrito.isBlank()) {
            _uiState.update { it.copy(errorCapitalMixto = true) }
            return
        }

        val esValida = ValidadorCapital.esCapitalValida(preguntaActual.paisCorrecto, textoEscrito)

        if (esValida) {
            val otras = ValidadorCapital.obtenerOtrasCapitales(preguntaActual.paisCorrecto, textoEscrito)
            val feedback = if (otras.isNotEmpty()) {
                "¡Correcto! Otras capitales: ${otras.joinToString(", ")}"
            } else {
                "¡Correcto!"
            }

            val respuesta = RespuestaQuiz(
                pregunta = preguntaActual,
                opcionSeleccionada = currentState.banderaSeleccionadaMixto ?: preguntaActual.paisCorrecto,
                esCorrecta = true,
                capitalEscrita = textoEscrito
            )

            _uiState.update {
                it.copy(
                    errorCapitalMixto = false,
                    feedbackMensajeMixto = feedback,
                    otrasCapitalesMixto = otras,
                    respuestasPorIndice = it.respuestasPorIndice + (it.indiceActual to respuesta)
                )
            }

            autoAvanzarJob?.cancel()
            autoAvanzarJob = viewModelScope.launch {
                delay(1200)
                avanzarSiguientePregunta()
            }
        } else {
            _uiState.update { it.copy(errorCapitalMixto = true) }
        }
    }

    /**
     * Botón Rendirse en Test Mixto: Cuenta el país como error, muestra la capital y avanza.
     */
    fun rendirseMixto() {
        val currentState = _uiState.value
        if (currentState.estaContestada || currentState.quizTerminado) return

        val preguntaActual = currentState.preguntaActual ?: return

        val respuesta = RespuestaQuiz(
            pregunta = preguntaActual,
            opcionSeleccionada = currentState.banderaSeleccionadaMixto ?: preguntaActual.paisCorrecto,
            esCorrecta = false,
            capitalEscrita = currentState.textoCapitalMixto.ifBlank { null },
            seHaRendido = true
        )

        _uiState.update {
            it.copy(
                feedbackMensajeMixto = "Te has rendido. La capital es: ${preguntaActual.paisCorrecto.capital}",
                respuestasPorIndice = it.respuestasPorIndice + (it.indiceActual to respuesta)
            )
        }

        autoAvanzarJob?.cancel()
        autoAvanzarJob = viewModelScope.launch {
            delay(1800)
            avanzarSiguientePregunta()
        }
    }

    private fun sincronizarEstadoPregunta(nuevoIndice: Int) {
        val respuesta = _uiState.value.respuestasPorIndice[nuevoIndice]
        if (respuesta != null) {
            val pregunta = respuesta.pregunta
            val feedback = if (respuesta.esCorrecta) {
                val otras = ValidadorCapital.obtenerOtrasCapitales(pregunta.paisCorrecto, respuesta.capitalEscrita ?: "")
                if (otras.isNotEmpty()) "¡Correcto! Otras capitales: ${otras.joinToString(", ")}" else "¡Correcto!"
            } else if (respuesta.falloEnBandera) {
                "¡Bandera incorrecta! La capital es: ${pregunta.paisCorrecto.capital}"
            } else {
                "La capital es: ${pregunta.paisCorrecto.capital}"
            }

            _uiState.update {
                it.copy(
                    indiceActual = nuevoIndice,
                    maxIndiceAlcanzado = maxOf(it.maxIndiceAlcanzado, nuevoIndice),
                    banderaSeleccionadaMixto = respuesta.opcionSeleccionada,
                    banderaEsCorrectaMixto = !respuesta.falloEnBandera,
                    textoCapitalMixto = respuesta.capitalEscrita ?: "",
                    errorCapitalMixto = false,
                    feedbackMensajeMixto = feedback,
                    otrasCapitalesMixto = if (respuesta.esCorrecta) {
                        ValidadorCapital.obtenerOtrasCapitales(pregunta.paisCorrecto, respuesta.capitalEscrita ?: "")
                    } else emptyList()
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    indiceActual = nuevoIndice,
                    maxIndiceAlcanzado = maxOf(it.maxIndiceAlcanzado, nuevoIndice),
                    banderaSeleccionadaMixto = null,
                    banderaEsCorrectaMixto = null,
                    textoCapitalMixto = "",
                    errorCapitalMixto = false,
                    feedbackMensajeMixto = null,
                    otrasCapitalesMixto = emptyList()
                )
            }
        }
    }

    fun retrocederPregunta() {
        autoAvanzarJob?.cancel()
        val currentState = _uiState.value
        if (currentState.puedeRetroceder) {
            sincronizarEstadoPregunta(currentState.indiceActual - 1)
        }
    }

    fun avanzarSiguientePregunta() {
        autoAvanzarJob?.cancel()
        val currentState = _uiState.value
        if (!currentState.puedeAvanzar) return

        if (currentState.esUltimaPregunta) {
            _uiState.update { it.copy(quizTerminado = true) }
        } else {
            sincronizarEstadoPregunta(currentState.indiceActual + 1)
        }
    }

    fun setMostrarDialogoSalir(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarDialogoSalir = mostrar) }
    }
}
