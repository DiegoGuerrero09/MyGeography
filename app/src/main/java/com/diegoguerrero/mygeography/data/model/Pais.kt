package com.diegoguerrero.mygeography.data.model

data class Pais(
    val codigo: String,
    val nombre: String,
    val capital: String,
    val continente: Continente,
    val esSoberano: Boolean,
    val estadoSoberano: String? = null
)
