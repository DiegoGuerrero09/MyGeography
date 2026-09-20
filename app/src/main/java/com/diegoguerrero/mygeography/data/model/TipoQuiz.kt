package com.diegoguerrero.mygeography.data.model

enum class TipoQuiz(val titulo: String, val descripcion: String, val totalPreguntas: Int) {
    BANDERAS(
        titulo = "Test de Banderas",
        descripcion = "254 naciones y territorios",
        totalPreguntas = 254
    ),
    CAPITALES(
        titulo = "Test de Capitales",
        descripcion = "195 países soberanos",
        totalPreguntas = 195
    )
}
