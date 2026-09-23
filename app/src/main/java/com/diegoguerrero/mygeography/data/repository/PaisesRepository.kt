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

    private val gruposBanderasSimilares: List<Set<String>> = listOf(
        // Cruz escandinava / nórdicos
        setOf("no", "se", "dk", "fi", "is", "ax", "fo"),
        // Cantón con Union Jack (británicas / commonwealth)
        setOf("gb", "au", "nz", "fj", "tv", "bm", "ky", "fk", "ms", "sh", "tc", "vg", "ck", "nu", "pn", "gs", "io"),
        // Centroamérica franjas azul-blanco-azul
        setOf("gt", "hn", "sv", "ni", "cr"),
        // Tricolores eslavas (blanco-azul-rojo)
        setOf("ru", "sk", "si", "hr", "rs", "cz"),
        // Países de Asia Central (-stán)
        setOf("kz", "kg", "tj", "tm", "uz", "af", "pk"),
        // Colores panárabes (rojo, blanco, negro, verde)
        setOf("eg", "iq", "sy", "ye", "jo", "ps", "kw", "ae", "sd", "ly"),
        // Colores panafricanos (verde, amarillo, rojo)
        setOf("sn", "ml", "gn", "cm", "cg", "cd", "bj", "tg", "gh", "et", "gw", "st"),
        // Gran Colombia (amarillo, azul, rojo)
        setOf("co", "ve", "ec"),
        // Cruz del Sur
        setOf("au", "nz", "ws", "pg", "br"),
        // Media luna y estrella / símbolos islámicos
        setOf("tr", "tn", "dz", "az", "my", "pk", "mr", "ly", "sg", "uz", "tm", "km"),
        // Rojo y blanco horizontales / verticales / bicolores
        setOf("at", "lv", "lb", "pe", "ca", "mc", "id", "pl", "sg", "mt", "bh", "qa", "ge"),
        // Azul y amarillo / azul-amarillo-rojo
        setOf("se", "ua", "kz", "pw", "ro", "td", "ad", "md"),
        // Tricolores verticales con verde/blanco/naranja o verde/blanco/rojo
        setOf("ie", "ci", "it", "mx", "ng", "dz", "sa"),
        // Negro, rojo y amarillo
        setOf("de", "be", "ao", "ug"),
        // Islas del Caribe / Antillas
        setOf("ag", "bs", "bb", "dm", "gd", "jm", "kn", "lc", "vc", "tt", "aw", "cw", "sx", "mf", "bl", "pr", "ai", "ms", "tc", "vg", "vi"),
        // Islas del Pacífico / Oceanía
        setOf("fm", "mh", "pw", "ki", "nr", "tv", "ws", "to", "vu", "sb", "fj", "ck", "nu", "tk"),
        // África Austral
        setOf("za", "na", "bw", "zw", "mz", "sz", "ls"),
        // África Oriental
        setOf("ke", "tz", "ug", "rw", "bi", "ss", "so", "dj", "er"),
        // Magreb / Norte de África
        setOf("ma", "dz", "tn", "ly", "eg", "eh", "mr"),
        // África Occidental
        setOf("sn", "gm", "gn", "gw", "sl", "lr", "ci", "ml", "bf", "ne", "ng", "bj", "tg"),
        // Sudeste Asiático
        setOf("th", "la", "kh", "vn", "mm", "my", "sg", "id", "ph", "bn", "tl"),
        // Asia Oriental
        setOf("cn", "jp", "kr", "kp", "tw", "mn", "hk", "mo"),
        // Sur de Asia
        setOf("in", "pk", "bd", "lk", "np", "bt", "mv"),
        // Países Bálticos
        setOf("ee", "lv", "lt"),
        // Balcanes
        setOf("gr", "al", "mk", "bg", "rs", "ba", "hr", "me", "xk", "ro"),
        // Europa Occidental
        setOf("fr", "nl", "be", "lu", "mc"),
        // Península Ibérica y Mediterráneo
        setOf("es", "pt", "ad", "gi"),
        // Islas Británicas
        setOf("gb", "im", "je", "gg", "ie"),
        // Cono Sur
        setOf("ar", "uy", "py", "cl", "bo", "br"),
        // Guayanas y norte de Sudamérica
        setOf("gy", "sr", "gf", "ve"),
        // Regiones polares / Antártida
        setOf("aq", "tf", "bv", "gs", "hm")
    )

    /**
     * Genera el Test Mixto:
     * - 8 opciones de banderas parecidas (1 correcta + 7 distractores) en 4 filas y 2 columnas.
     * - Se priorizan banderas con estructuras, símbolos o colores similares, o países vecinos.
     */
    fun generarQuizMixto(
        region: RegionQuiz = RegionQuiz.GLOBAL,
        incluirDependientes: Boolean = true
    ): List<QuizPregunta> {
        val universo = if (incluirDependientes) obtenerTodos() else obtenerIndependientes()
        val preguntasBarajadas = filtrarPorRegion(universo, region).shuffled()

        val universoPorCodigo = universo.associateBy { it.codigo }

        return preguntasBarajadas.mapIndexed { index, paisCorrecto ->
            val distractores = mutableListOf<Pais>()

            // 1. Obtener candidatos de grupos con banderas similares / países vecinos
            val candidatosSimilares = gruposBanderasSimilares
                .filter { paisCorrecto.codigo in it }
                .flatMap { it }
                .distinct()
                .filter { it != paisCorrecto.codigo }
                .mapNotNull { universoPorCodigo[it] }
                .filter { !sonBanderasConfusas(it.codigo, paisCorrecto.codigo) }
                .shuffled()

            for (candidato in candidatosSimilares) {
                if (distractores.none { it.codigo == candidato.codigo || sonBanderasConfusas(it.codigo, candidato.codigo) }) {
                    distractores.add(candidato)
                    if (distractores.size == 7) break
                }
            }

            // 2. Si se necesitan más distractores, completar con países de la misma región o continente
            if (distractores.size < 7) {
                val candidatosRegion = universo
                    .filter { it.codigo != paisCorrecto.codigo && perteneceARegion(it, region) && !sonBanderasConfusas(it.codigo, paisCorrecto.codigo) }
                    .shuffled()

                for (candidato in candidatosRegion) {
                    if (distractores.none { it.codigo == candidato.codigo || sonBanderasConfusas(it.codigo, candidato.codigo) }) {
                        distractores.add(candidato)
                        if (distractores.size == 7) break
                    }
                }
            }

            // 3. Si aún faltan distractores, completar con el resto del universo
            if (distractores.size < 7) {
                val restoUniverso = universo
                    .filter { it.codigo != paisCorrecto.codigo && !sonBanderasConfusas(it.codigo, paisCorrecto.codigo) }
                    .shuffled()

                for (candidato in restoUniverso) {
                    if (distractores.none { it.codigo == candidato.codigo || sonBanderasConfusas(it.codigo, candidato.codigo) }) {
                        distractores.add(candidato)
                        if (distractores.size == 7) break
                    }
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
}
