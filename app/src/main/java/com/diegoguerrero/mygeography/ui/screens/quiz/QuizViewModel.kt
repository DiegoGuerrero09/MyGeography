package com.diegoguerrero.mygeography.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoguerrero.mygeography.data.model.Pais
import com.diegoguerrero.mygeography.data.model.QuizPregunta
import com.diegoguerrero.mygeography.data.model.RespuestaQuiz
import com.diegoguerrero.mygeography.data.model.TipoQuiz
import com.diegoguerrero.mygeography.data.repository.PaisesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val tipoQuiz: TipoQuiz = TipoQuiz.BANDERAS,
    val preguntas: List<QuizPregunta> = emptyList(),
    val indiceActual: Int = 0,
    val opcionSeleccionada: Pais? = null,
    val esRespuestaCorrecta: Boolean? = null,
    val estaEvaluando: Boolean = false,
    val respuestas: List<RespuestaQuiz> = emptyList(),
    val quizTerminado: Boolean = false,
    val mostrarDialogoSalir: Boolean = false
) {
    val preguntaActual: QuizPregunta?
        get() = preguntas.getOrNull(indiceActual)

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

    fun iniciarQuiz(tipo: TipoQuiz) {
        autoAvanzarJob?.cancel()
        val preguntas = when (tipo) {
            TipoQuiz.BANDERAS -> repository.generarQuizBanderas()
            TipoQuiz.CAPITALES -> repository.generarQuizCapitales()
        }

        _uiState.value = QuizUiState(
            tipoQuiz = tipo,
            preguntas = preguntas,
            indiceActual = 0,
            opcionSeleccionada = null,
            esRespuestaCorrecta = null,
            estaEvaluando = false,
            respuestas = emptyList(),
            quizTerminado = false,
            mostrarDialogoSalir = false
        )
    }

    fun seleccionarOpcion(opcion: Pais) {
        val currentState = _uiState.value
        if (currentState.estaEvaluando || currentState.quizTerminado) return

        val preguntaActual = currentState.preguntaActual ?: return
        val esCorrecta = opcion.codigo == preguntaActual.paisCorrecto.codigo

        val nuevaRespuesta = RespuestaQuiz(
            pregunta = preguntaActual,
            opcionSeleccionada = opcion,
            esCorrecta = esCorrecta
        )

        val nuevasRespuestas = currentState.respuestas + nuevaRespuesta

        _uiState.update {
            it.copy(
                opcionSeleccionada = opcion,
                esRespuestaCorrecta = esCorrecta,
                estaEvaluando = true,
                respuestas = nuevasRespuestas
            )
        }

        autoAvanzarJob = viewModelScope.launch {
            delay(1100)
            avanzarSiguientePregunta()
        }
    }

    fun avanzarSiguientePregunta() {
        autoAvanzarJob?.cancel()
        val currentState = _uiState.value
        if (!currentState.estaEvaluando) return

        if (currentState.indiceActual + 1 < currentState.totalPreguntas) {
            _uiState.update {
                it.copy(
                    indiceActual = it.indiceActual + 1,
                    opcionSeleccionada = null,
                    esRespuestaCorrecta = null,
                    estaEvaluando = false
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    estaEvaluando = false,
                    quizTerminado = true
                )
            }
        }
    }

    fun setMostrarDialogoSalir(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarDialogoSalir = mostrar) }
    }
}
