package com.diegoguerrero.mygeography.data.model

data class RespuestaQuiz(
    val pregunta: QuizPregunta,
    val opcionSeleccionada: Pais,
    val esCorrecta: Boolean,
    val capitalEscrita: String? = null,
    val falloEnBandera: Boolean = false,
    val seHaRendido: Boolean = false
)
