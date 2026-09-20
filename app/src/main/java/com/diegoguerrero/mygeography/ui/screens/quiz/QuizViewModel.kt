package com.diegoguerrero.mygeography.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoguerrero.mygeography.data.model.Pais
import com.diegoguerrero.mygeography.data.model.QuizPregunta
import com.diegoguerrero.mygeography.data.model.RegionQuiz
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
    val region: RegionQuiz = RegionQuiz.GLOBAL,
    val incluirDependientes: Boolean = true,
    val preguntas: List<QuizPregunta> = emptyList(),
    val indiceActual: Int = 0,
    val respuestasPorIndice: Map<Int, RespuestaQuiz> = emptyMap(),
    val quizTerminado: Boolean = false,
    val mostrarDialogoSalir: Boolean = false
) {
    val preguntaActual: QuizPregunta?
        get() = preguntas.getOrNull(indiceActual)

    val respuestaActual: RespuestaQuiz?
        get() = respuestasPorIndice[indiceActual]

    val estaContestada: Boolean
        get() = respuestaActual != null

    val opcionSeleccionada: Pais?
        get() = respuestaActual?.opcionSeleccionada

    val esRespuestaCorrecta: Boolean?
        get() = respuestaActual?.esCorrecta

    val estaEvaluando: Boolean
        get() = estaContestada

    val puedeAvanzar: Boolean
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

    fun iniciarQuiz(
        tipo: TipoQuiz,
        region: RegionQuiz = RegionQuiz.GLOBAL,
        incluirDependientes: Boolean = true
    ) {
        val preguntas = when (tipo) {
            TipoQuiz.BANDERAS -> repository.generarQuizBanderas(region, incluirDependientes)
            TipoQuiz.CAPITALES -> repository.generarQuizCapitales(region, incluirDependientes)
        }

        _uiState.value = QuizUiState(
            tipoQuiz = tipo,
            region = region,
            incluirDependientes = incluirDependientes,
            preguntas = preguntas,
            indiceActual = 0,
            respuestasPorIndice = emptyMap(),
            quizTerminado = false,
            mostrarDialogoSalir = false
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
    }

    fun retrocederPregunta() {
        val currentState = _uiState.value
        if (currentState.puedeRetroceder) {
            _uiState.update { it.copy(indiceActual = it.indiceActual - 1) }
        }
    }

    fun avanzarSiguientePregunta() {
        val currentState = _uiState.value
        if (!currentState.puedeAvanzar) return

        if (currentState.esUltimaPregunta) {
            _uiState.update { it.copy(quizTerminado = true) }
        } else {
            _uiState.update { it.copy(indiceActual = it.indiceActual + 1) }
        }
    }

    fun setMostrarDialogoSalir(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarDialogoSalir = mostrar) }
    }
}
