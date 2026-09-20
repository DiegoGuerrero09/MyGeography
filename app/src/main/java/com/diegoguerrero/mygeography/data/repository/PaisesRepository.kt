package com.diegoguerrero.mygeography.data.repository

import com.diegoguerrero.mygeography.data.datasource.PaisesData
import com.diegoguerrero.mygeography.data.model.Continente
import com.diegoguerrero.mygeography.data.model.Pais
import com.diegoguerrero.mygeography.data.model.QuizPregunta
import com.diegoguerrero.mygeography.data.model.RegionQuiz

class PaisesRepository {

    val codigosSudamerica = setOf(
        "ar", "bo", "br", "cl", "co", "ec", "fk", "gf", "gy", "pe", "py", "sr", "uy", "ve"
    )

    val codigosAntartida = setOf(
        "aq", "bv", "gs", "hm", "tf"
    )

    fun obtenerTodos(): List<Pais> = PaisesData.listaPaises

    fun obtenerSoberanos(): List<Pais> = PaisesData.listaPaises.filter { it.esSoberano }

    fun perteneceARegion(pais: Pais, region: RegionQuiz): Boolean {
        return when (region) {
            RegionQuiz.GLOBAL -> true
            RegionQuiz.EUROPA -> pais.continente == Continente.EUROPA
            RegionQuiz.AFRICA -> pais.continente == Continente.AFRICA
            RegionQuiz.ASIA -> pais.continente == Continente.ASIA
            RegionQuiz.OCEANIA -> pais.continente == Continente.OCEANIA
            RegionQuiz.SUDAMERICA_ANTARTIDA -> {
                pais.codigo in codigosSudamerica || pais.codigo in codigosAntartida || pais.continente == Continente.ANTARTIDA
            }
            RegionQuiz.NORTEAMERICA_CENTROAMERICA -> {
                pais.continente == Continente.AMERICA && pais.codigo !in codigosSudamerica
            }
        }
    }

    fun filtrarPorRegion(paises: List<Pais>, region: RegionQuiz): List<Pais> {
        return paises.filter { perteneceARegion(it, region) }
    }

    /**
     * Genera el Test de Banderas (por región o global)
     * 12 opciones (1 correcta + 11 distractores globales) en cuadrícula 6x2
     */
    fun generarQuizBanderas(region: RegionQuiz = RegionQuiz.GLOBAL): List<QuizPregunta> {
        val universoGlobal = obtenerTodos()
        val preguntasBarajadas = filtrarPorRegion(universoGlobal, region).shuffled()

        return preguntasBarajadas.mapIndexed { index, paisCorrecto ->
            val distractores = universoGlobal
                .filter { it.codigo != paisCorrecto.codigo }
                .shuffled()
                .take(11)

            val opciones = (distractores + paisCorrecto).shuffled()

            QuizPregunta(
                numeroPregunta = index + 1,
                paisCorrecto = paisCorrecto,
                opciones = opciones
            )
        }
    }

    private val collatorEspanol: java.text.Collator = java.text.Collator.getInstance(java.util.Locale("es", "ES")).apply {
        strength = java.text.Collator.SECONDARY
    }

    /**
     * Genera el Test de Capitales (por región o global)
     * Incluye todas las naciones y territorios (254 territorios o por región)
     * 12 opciones (1 correcta + 11 distractores globales únicos) ordenadas alfabéticamente en español
     */
    fun generarQuizCapitales(region: RegionQuiz = RegionQuiz.GLOBAL): List<QuizPregunta> {
        val universoGlobal = obtenerTodos()
        val preguntasBarajadas = filtrarPorRegion(universoGlobal, region).shuffled()

        return preguntasBarajadas.mapIndexed { index, paisCorrecto ->
            val distractores = universoGlobal
                .filter { it.codigo != paisCorrecto.codigo && it.capital != paisCorrecto.capital }
                .distinctBy { it.capital }
                .shuffled()
                .take(11)

            // Opciones de capitales ordenadas alfabéticamente en español
            val opciones = (distractores + paisCorrecto).sortedWith(
                compareBy(collatorEspanol) { it.capital }
            )

            QuizPregunta(
                numeroPregunta = index + 1,
                paisCorrecto = paisCorrecto,
                opciones = opciones
            )
        }
    }
}
