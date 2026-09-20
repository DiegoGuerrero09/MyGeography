package com.diegoguerrero.mygeography.data.model

enum class RegionQuiz(
    val id: String,
    val nombre: String,
    val abreviatura: String
) {
    GLOBAL("global", "Global", "Global"),
    EUROPA("europa", "Europa", "Europa"),
    AFRICA("africa", "África", "África"),
    ASIA("asia", "Asia", "Asia"),
    OCEANIA("oceania", "Oceanía", "Oceanía"),
    SUDAMERICA_ANTARTIDA("sudamerica_antartida", "Sudamérica & Antártida", "Sudamérica & Antártida"),
    NORTEAMERICA_CENTROAMERICA("norteamerica_centroamerica", "Norteamérica & Centroamérica", "Norteamérica & Centroamérica")
}
