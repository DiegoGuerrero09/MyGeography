package com.diegoguerrero.mygeography.data.model

enum class RegionQuiz(
    val id: String,
    val nombre: String,
    val abreviatura: String
) {
    GLOBAL("global", "Global", "Global"),
    AFRICA("africa", "África", "África"),
    ASIA("asia", "Asia", "Asia"),
    EUROPA("europa", "Europa", "Europa"),
    NORTEAMERICA_CENTROAMERICA("norteamerica_centroamerica", "Norteamérica & Centroamérica", "Norteamérica & Centroamérica"),
    OCEANIA("oceania", "Oceanía", "Oceanía"),
    SUDAMERICA_ANTARTIDA("sudamerica_antartida", "Sudamérica & Antártida", "Sudamérica & Antártida")
}
