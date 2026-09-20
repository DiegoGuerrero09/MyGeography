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

    private val gruposBanderasConfusas: List<Set<String>> = listOf(
        setOf("no", "sj", "bv"), // Noruega, Svalbard y Jan Mayen, Isla Bouvet
        setOf("td", "ro"),       // Chad, Rumania
        setOf("mc", "id"),       // Mónaco, Indonesia
        setOf("us", "um"),       // Estados Unidos, Islas Ultramarinas Menores de EE.UU.
        setOf("fr", "mf"),       // Francia, San Martín (Francia)
        setOf("ie", "ci"),       // Irlanda, Costa de Marfil
        setOf("lu", "nl")        // Luxemburgo, Países Bajos
    )

    private fun sonBanderasConfusas(codigoA: String, codigoB: String): Boolean {
        if (codigoA == codigoB) return true
        return gruposBanderasConfusas.any { grupo ->
            codigoA in grupo && codigoB in grupo
        }
    }

    fun obtenerIndependientes(): List<Pais> = PaisesData.listaPaises.filter { it.esSoberano }

    /**
     * Genera el Test de Banderas (por región o global).
     * @param region Región geográfica seleccionada o GLOBAL.
     * @param incluirDependientes Si es true, incluye los 59 países dependientes además de los 195 independientes.
     */
    fun generarQuizBanderas(
        region: RegionQuiz = RegionQuiz.GLOBAL,
        incluirDependientes: Boolean = true
    ): List<QuizPregunta> {
        val universo = if (incluirDependientes) obtenerTodos() else obtenerIndependientes()
        val preguntasBarajadas = filtrarPorRegion(universo, region).shuffled()

        return preguntasBarajadas.mapIndexed { index, paisCorrecto ->
            val distractores = mutableListOf<Pais>()
            val candidatos = universo
                .filter { it.codigo != paisCorrecto.codigo && !sonBanderasConfusas(it.codigo, paisCorrecto.codigo) }
                .shuffled()

            for (candidato in candidatos) {
                if (distractores.none { sonBanderasConfusas(it.codigo, candidato.codigo) }) {
                    distractores.add(candidato)
                    if (distractores.size == 11) break
                }
            }

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
     * Genera el Test de Capitales (por región o global).
     * @param region Región geográfica seleccionada o GLOBAL.
     * @param incluirDependientes Si es true, incluye países dependientes.
     */
    fun generarQuizCapitales(
        region: RegionQuiz = RegionQuiz.GLOBAL,
        incluirDependientes: Boolean = true
    ): List<QuizPregunta> {
        val universo = if (incluirDependientes) obtenerTodos() else obtenerIndependientes()
        val preguntasBarajadas = filtrarPorRegion(universo, region).shuffled()

        return preguntasBarajadas.mapIndexed { index, paisCorrecto ->
            val distractores = universo
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
