package com.diegoguerrero.mygeography.data.model

data class QuizPregunta(
    val numeroPregunta: Int,
    val paisCorrecto: Pais,
    val opciones: List<Pais> // Exactly 8 distinct options (1 correct + 7 distractors)
)
