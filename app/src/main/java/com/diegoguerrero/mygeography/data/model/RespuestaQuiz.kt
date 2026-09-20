package com.diegoguerrero.mygeography.data.model

data class RespuestaQuiz(
    val pregunta: QuizPregunta,
    val opcionSeleccionada: Pais,
    val esCorrecta: Boolean
)
