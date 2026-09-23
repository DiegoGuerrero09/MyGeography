package com.diegoguerrero.mygeography.data.model

enum class TipoQuiz(val titulo: String, val descripcion: String, val totalPreguntas: Int) {
    BANDERAS(
        titulo = "Test de banderas",
        descripcion = "254 territorios",
        totalPreguntas = 254
    ),
    CAPITALES(
        titulo = "Test de capitales",
        descripcion = "195 países",
        totalPreguntas = 195
    ),
    MIXTO(
        titulo = "Test mixto",
        descripcion = "254 países",
        totalPreguntas = 254
    )
}
