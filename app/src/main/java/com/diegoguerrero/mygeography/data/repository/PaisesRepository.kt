package com.diegoguerrero.mygeography.data.repository

import com.diegoguerrero.mygeography.data.datasource.PaisesData
import com.diegoguerrero.mygeography.data.model.Pais
import com.diegoguerrero.mygeography.data.model.QuizPregunta

class PaisesRepository {

    fun obtenerTodos(): List<Pais> = PaisesData.listaPaises

    fun obtenerSoberanos(): List<Pais> = PaisesData.listaPaises.filter { it.esSoberano }

    fun generarQuizBanderas(): List<QuizPregunta> {
        val universo = obtenerTodos()
        val preguntasBarajadas = universo.shuffled()

        return preguntasBarajadas.mapIndexed { index, paisCorrecto ->
            val distractores = universo
                .filter { it.codigo != paisCorrecto.codigo }
                .shuffled()
                .take(8)

            val opciones = (distractores + paisCorrecto).shuffled()

            QuizPregunta(
                numeroPregunta = index + 1,
                paisCorrecto = paisCorrecto,
                opciones = opciones
            )
        }
    }

    fun generarQuizCapitales(): List<QuizPregunta> {
        val universo = obtenerSoberanos()
        val preguntasBarajadas = universo.shuffled()

        return preguntasBarajadas.mapIndexed { index, paisCorrecto ->
            val distractores = universo
                .filter { it.codigo != paisCorrecto.codigo }
                .shuffled()
                .take(8)

            val opciones = (distractores + paisCorrecto).shuffled()

            QuizPregunta(
                numeroPregunta = index + 1,
                paisCorrecto = paisCorrecto,
                opciones = opciones
            )
        }
    }
}
