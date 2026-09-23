package com.diegoguerrero.mygeography.data.util

import com.diegoguerrero.mygeography.data.model.Pais
import java.text.Normalizer

object ValidadorCapital {

    /**
     * Normaliza un texto para comparación:
     * - Convierte a minúsculas
     * - Reemplaza caracteres especiales nórdicos, eslavos, etc. (Ø -> o, Ç -> c, etc.)
     * - Elimina tildes y signos diacríticos (Valparaíso -> valparaiso, Bogotá -> bogota)
     * - Convierte guiones y caracteres de unión en espacios
     * - Elimina signos de puntuación como comillas, apóstrofes o puntos
     * - Colapsa espacios redundantes
     */
    fun normalizar(texto: String): String {
        var s = texto.lowercase().trim()

        // Reemplazo explícito de caracteres especiales antes de NFD
        s = s.replace("ø", "o")
            .replace("ç", "c")
            .replace("æ", "ae")
            .replace("œ", "oe")
            .replace("ß", "ss")
            .replace("ð", "d")
            .replace("þ", "th")
            .replace("ł", "l")
            .replace("đ", "d")

        // Descomponer tildes y diacríticos y eliminarlos
        s = Normalizer.normalize(s, Normalizer.Form.NFD)
        s = s.replace("\\p{M}+".toRegex(), "")

        // Guiones, barras y subrayados convertidos a espacios
        s = s.replace("-", " ")
            .replace("_", " ")

        // Eliminar apóstrofes y puntuación
        s = s.replace("'", "")
            .replace("’", "")
            .replace("`", "")
            .replace(".", "")
            .replace(",", "")
            .replace("(", "")
            .replace(")", "")

        // Colapsar espacios múltiples y recortar extremos
        s = s.replace("\\s+".toRegex(), " ").trim()
        return s
    }

    /**
     * Diccionario de variantes conocidas de capitales por código de país:
     * Nombres en inglés, variaciones ortográficas (como Dakar/Dacar, Pekín/Beijing, Kiev/Kyiv),
     * omisiones comunes de "Ciudad de", etc.
     */
    private val variantesPorCodigoPais: Map<String, List<String>> = mapOf(
        // África
        "sn" to listOf("Dakar", "Dacar"),
        "bd" to listOf("Daca", "Dhaka"),
        "bf" to listOf("Uagadugú", "Ouagadougou", "Uagadugu"),
        "kh" to listOf("Nom Pen", "Phnom Penh"),
        "bt" to listOf("Timbu", "Thimphu"),
        "cm" to listOf("Yaundé", "Yaounde"),
        "dj" to listOf("Yibuti", "Djibouti", "Ciudad de Yibuti", "Djibouti City"),
        "cd" to listOf("Kinsasa", "Kinshasa"),
        "cg" to listOf("Brazzaville"),
        "ci" to listOf("Yamusukro", "Yamoussoukro", "Abiyán", "Abidjan"),
        "bi" to listOf("Gitega", "Buyumbura", "Bujumbura"),
        "gq" to listOf("Ciudad de la Paz", "Malabo"),
        "za" to listOf("Pretoria", "Ciudad del Cabo", "Cape Town", "Bloemfontein"),
        "tz" to listOf("Dodoma", "Dar es-Salam", "Dar es Salaam"),
        "sz" to listOf("Mbabane", "Lobamba"),
        "dz" to listOf("Argel", "Algiers"),
        "tn" to listOf("Túnez", "Tunis"),
        "ly" to listOf("Trípoli", "Tripoli"),
        "ma" to listOf("Rabat"),
        "eg" to listOf("El Cairo", "Cairo"),
        "et" to listOf("Adís Abeba", "Addis Ababa"),
        "so" to listOf("Mogadiscio", "Mogadishu"),
        "sd" to listOf("Jartum", "Khartoum"),
        "ss" to listOf("Yuba", "Juba"),
        "er" to listOf("Asmara"),
        "td" to listOf("Yamena", "NDjamena", "N'Djamena"),
        "ne" to listOf("Niamey"),
        "ml" to listOf("Bamako"),
        "mr" to listOf("Nuakchot", "Nouakchott"),
        "km" to listOf("Moroni"),
        "mg" to listOf("Antananarivo"),
        "sc" to listOf("Victoria"),
        "mu" to listOf("Port Louis", "Puerto Luis"),
        "cv" to listOf("Praia"),
        "st" to listOf("Santo Tomé", "Sao Tome"),
        "gw" to listOf("Bisáu", "Bissau"),
        "gn" to listOf("Conakri", "Conakry"),
        "sl" to listOf("Freetown"),
        "lr" to listOf("Monrovia"),
        "tg" to listOf("Lomé", "Lome"),
        "bj" to listOf("Porto Novo", "Cotonú", "Cotonou"),
        "ng" to listOf("Abuya", "Abuja"),
        "cf" to listOf("Bangui"),
        "ga" to listOf("Libreville"),
        "ao" to listOf("Luanda"),
        "zm" to listOf("Lusaka"),
        "zw" to listOf("Harare"),
        "mw" to listOf("Lilongüe", "Lilongwe"),
        "mz" to listOf("Maputo"),
        "bw" to listOf("Gaborone"),
        "na" to listOf("Windhoek"),
        "ls" to listOf("Maseru"),
        "rw" to listOf("Kigali"),
        "ug" to listOf("Kampala"),
        "ke" to listOf("Nairobi"),

        // Europa
        "de" to listOf("Berlín", "Berlin"),
        "at" to listOf("Viena", "Vienna"),
        "be" to listOf("Bruselas", "Brussels", "Bruxelles"),
        "dk" to listOf("Copenhague", "Copenhagen", "Kobenhavn"),
        "es" to listOf("Madrid"),
        "fr" to listOf("París", "Paris"),
        "gb" to listOf("Londres", "London"),
        "gr" to listOf("Atenas", "Athens"),
        "ie" to listOf("Dublín", "Dublin"),
        "is" to listOf("Reikiavik", "Reykjavik"),
        "it" to listOf("Roma", "Rome"),
        "nl" to listOf("Ámsterdam", "Amsterdam", "La Haya", "The Hague", "Den Haag"),
        "no" to listOf("Oslo"),
        "pl" to listOf("Varsovia", "Warsaw", "Warszawa"),
        "pt" to listOf("Lisboa", "Lisbon"),
        "cz" to listOf("Praga", "Prague", "Praha"),
        "ro" to listOf("Bucarest", "Bucharest", "Bucuresti"),
        "ru" to listOf("Moscú", "Moscow", "Moskva"),
        "se" to listOf("Estocolmo", "Stockholm"),
        "ch" to listOf("Berna"),
        "ua" to listOf("Kiev", "Kyiv"),
        "rs" to listOf("Belgrado", "Belgrade", "Beograd"),
        "hr" to listOf("Zagreb"),
        "ba" to listOf("Sarajevo"),
        "si" to listOf("Liubliana", "Ljubljana"),
        "sk" to listOf("Bratislava"),
        "bg" to listOf("Sofía", "Sofia"),
        "hu" to listOf("Budapest"),
        "by" to listOf("Minsk"),
        "lt" to listOf("Vilna", "Vilnius"),
        "lv" to listOf("Riga"),
        "ee" to listOf("Tallin", "Tallinn"),
        "fi" to listOf("Helsinki"),
        "al" to listOf("Tirana"),
        "mk" to listOf("Skopie", "Skopje"),
        "md" to listOf("Chisináu", "Chisinau", "Kishinev"),
        "cy" to listOf("Nicosia", "Lefkosia"),
        "lu" to listOf("Luxemburgo", "Luxembourg"),
        "mc" to listOf("Mónaco", "Monaco"),
        "ad" to listOf("Andorra la vieja", "Andorra la Vieja", "Andorra la Vella"),
        "va" to listOf("Ciudad del Vaticano", "Vaticano", "Vatican City"),
        "sm" to listOf("San Marino"),
        "li" to listOf("Vaduz"),
        "mt" to listOf("La Valeta", "Valletta", "Valeta"),
        "me" to listOf("Podgorica"),
        "xk" to listOf("Pristina", "Prishtina"),

        // Asia
        "cn" to listOf("Pekín", "Pekin", "Beijing"),
        "jp" to listOf("Tokio", "Tokyo"),
        "in" to listOf("Nueva Delhi", "New Delhi", "Delhi"),
        "kr" to listOf("Seúl", "Seoul"),
        "kp" to listOf("Pionyang", "Pyongyang"),
        "id" to listOf("Nusantara", "Yakarta", "Jakarta"),
        "my" to listOf("Kuala Lumpur", "Putrajaya"),
        "sg" to listOf("Singapur", "Singapore"),
        "th" to listOf("Bangkok"),
        "vn" to listOf("Hanói", "Hanoi"),
        "ph" to listOf("Manila"),
        "mm" to listOf("Naipyidó", "Naypyidaw", "Nay Pyi Taw"),
        "la" to listOf("Vientián", "Vientiane"),
        "mn" to listOf("Ulán Bator", "Ulaanbaatar", "Ulan Bator"),
        "kz" to listOf("Astaná", "Astana", "Nur-Sultán", "Nur Sultan", "Nursultan"),
        "uz" to listOf("Taskent", "Tashkent"),
        "tm" to listOf("Asjabad", "Ashgabat"),
        "kg" to listOf("Biskek", "Bishkek"),
        "tj" to listOf("Dusambé", "Dushanbe"),
        "af" to listOf("Kabul"),
        "pk" to listOf("Islamabad"),
        "np" to listOf("Katmandú", "Kathmandu"),
        "lk" to listOf("Sri Jayawardenepura Kotte", "Colombo", "Kotte"),
        "mv" to listOf("Malé", "Male"),
        "ir" to listOf("Teherán", "Tehran"),
        "iq" to listOf("Bagdad", "Baghdad"),
        "sy" to listOf("Damasco", "Damascus"),
        "lb" to listOf("Beirut"),
        "jo" to listOf("Amán", "Amman"),
        "il" to listOf("Jerusalén", "Jerusalem"),
        "ps" to listOf("Jerusalén Este", "Ramala", "Ramallah", "East Jerusalem"),
        "sa" to listOf("Riad", "Riyadh"),
        "ae" to listOf("Abu Dabi", "Abu Dhabi"),
        "qa" to listOf("Doha"),
        "bh" to listOf("Manama"),
        "kw" to listOf("Kuwait", "Ciudad de Kuwait", "Kuwait City"),
        "om" to listOf("Mascate", "Muscat"),
        "ye" to listOf("Saná", "Sanaa", "Adén", "Aden"),
        "ge" to listOf("Tiflis", "Tbilisi"),
        "am" to listOf("Ereván", "Yerevan"),
        "az" to listOf("Bakú", "Baku"),
        "tr" to listOf("Ankara"),

        // América
        "us" to listOf("Washington D.C.", "Washington DC", "Washington D C"),
        "ca" to listOf("Ottawa"),
        "mx" to listOf("Ciudad de México", "Mexico", "Mexico City", "CDMX"),
        "gt" to listOf("Ciudad de Guatemala", "Guatemala", "Guatemala City"),
        "bz" to listOf("Belmopán", "Belmopan"),
        "sv" to listOf("San Salvador"),
        "hn" to listOf("Tegucigalpa"),
        "ni" to listOf("Managua"),
        "cr" to listOf("San José", "San Jose"),
        "pa" to listOf("Ciudad de Panamá", "Panamá", "Panama City", "Panama"),
        "cu" to listOf("La Habana", "Havana", "Habana"),
        "bs" to listOf("Nasáu", "Nassau"),
        "ht" to listOf("Puerto Príncipe", "Port-au-Prince", "Port au Prince"),
        "do" to listOf("Santo Domingo"),
        "jm" to listOf("Kingston"),
        "co" to listOf("Bogotá", "Bogota"),
        "ve" to listOf("Caracas"),
        "ec" to listOf("Quito"),
        "pe" to listOf("Lima"),
        "bo" to listOf("Sucre", "La Paz"),
        "br" to listOf("Brasilia"),
        "py" to listOf("Asunción", "Asuncion"),
        "uy" to listOf("Montevideo"),
        "ar" to listOf("Buenos Aires"),
        "cl" to listOf("Santiago", "Valparaíso", "Valparaiso"),
        "gy" to listOf("Georgetown"),
        "sr" to listOf("Paramaribo"),
        "tt" to listOf("Puerto España", "Port of Spain"),
        "bb" to listOf("Bridgetown"),
        "lc" to listOf("Castries"),
        "vc" to listOf("Kingstown"),
        "gd" to listOf("Saint George", "Saint George's", "St George"),
        "ag" to listOf("Saint John", "Saint John's", "St John"),
        "dm" to listOf("Roseau"),
        "kn" to listOf("Basseterre"),

        // Oceanía
        "au" to listOf("Canberra"),
        "nz" to listOf("Wellington"),
        "pg" to listOf("Puerto Moresby", "Port Moresby"),
        "fj" to listOf("Suva"),
        "sb" to listOf("Honiara"),
        "vu" to listOf("Port Vila", "Puerto Vila"),
        "ws" to listOf("Apia"),
        "to" to listOf("Nukualofa", "Nuku'alofa"),
        "ki" to listOf("Tarawa Sur", "South Tarawa"),
        "tv" to listOf("Funafuti"),
        "nr" to listOf("Yaren"),
        "mh" to listOf("Majuro"),
        "fm" to listOf("Palikir"),
        "pw" to listOf("Ngerulmud", "Melekeok"),

        // Territorios dependientes y autónomos
        "gl" to listOf("Nuuk", "Godthab"),
        "fo" to listOf("Tórshavn", "Torshavn"),
        "ax" to listOf("Mariehamn"),
        "gi" to listOf("Gibraltar"),
        "im" to listOf("Douglas"),
        "je" to listOf("Saint Helier", "St Helier"),
        "gg" to listOf("Saint Peter Port", "St Peter Port"),
        "bm" to listOf("Hamilton"),
        "ky" to listOf("George Town"),
        "fk" to listOf("Puerto Argentino", "Stanley"),
        "pr" to listOf("San Juan"),
        "vi" to listOf("Carlota Amalia", "Charlotte Amalie"),
        "vg" to listOf("Road Town"),
        "aw" to listOf("Oranjestad"),
        "cw" to listOf("Willemstad"),
        "sx" to listOf("Philipsburg"),
        "mf" to listOf("Marigot"),
        "bl" to listOf("Gustavia"),
        "gp" to listOf("Basse-Terre", "Basse Terre"),
        "mq" to listOf("Fort-de-France", "Fort de France"),
        "gf" to listOf("Cayena", "Cayenne"),
        "pm" to listOf("San Pedro", "Saint-Pierre", "Saint Pierre"),
        "re" to listOf("Saint-Denis", "Saint Denis"),
        "yt" to listOf("Mamoudzou"),
        "nc" to listOf("Numea", "Noumea"),
        "pf" to listOf("Papeete"),
        "wf" to listOf("Mata-Utu", "Mata Utu"),
        "tc" to listOf("Cockburn Town"),
        "ai" to listOf("El Valle", "The Valley"),
        "ms" to listOf("Plymouth", "Brades"),
        "sh" to listOf("Jamestown"),
        "io" to listOf("Diego García", "Diego Garcia"),
        "tw" to listOf("Taipéi", "Taipei"),
        "hk" to listOf("Hong Kong", "Victoria"),
        "mo" to listOf("Macao", "Macau"),
        "gu" to listOf("Agaña", "Hagatna", "Hagåtña"),
        "mp" to listOf("Saipán", "Saipan"),
        "as" to listOf("Pago Pago"),
        "ck" to listOf("Avarua"),
        "nu" to listOf("Alofi"),
        "tk" to listOf("Nukunonu", "Fakaofo", "Atafu"),
        "eh" to listOf("El Aaiún", "El Aaiun", "Laayoune"),
        "gs" to listOf("King Edward Point", "Grytviken", "Punto Grytviken"),
        "tf" to listOf("Port-aux-Français", "Port aux Francais"),
        "aq" to listOf("Sin capital", "Ninguna", "No tiene", "None"),
        "bv" to listOf("Sin capital", "Ninguna", "No tiene", "None"),
        "hm" to listOf("Sin capital", "Ninguna", "No tiene", "None"),
        "um" to listOf("Sin capital", "Ninguna", "No tiene", "None")
    )

    /**
     * Devuelve true si la entrada del usuario coincide con la capital o alguna de sus variantes válidas.
     */
    fun esCapitalValida(pais: Pais, entradaUsuario: String): Boolean {
        val entradaNorm = normalizar(entradaUsuario)
        if (entradaNorm.isBlank()) return false

        // Exclusiones explícitas requeridas:
        // - En Kiribati ("ki"), "Tarawa" sola NO es válida, solo "Tarawa Sur"
        if (pais.codigo == "ki" && entradaNorm == "tarawa") {
            return false
        }
        // - En Suiza ("ch"), "Bern" NO es válida, solo "Berna"
        if (pais.codigo == "ch" && entradaNorm == "bern") {
            return false
        }
        // - En Andorra ("ad"), "Andorra" sola NO es válida, solo "Andorra la Vieja" o "Andorra la Vella"
        if (pais.codigo == "ad" && entradaNorm == "andorra") {
            return false
        }
        // - En Estados Unidos ("us"), "Washington" sola NO es válida, solo "Washington DC" o "Washington D.C."
        if (pais.codigo == "us" && entradaNorm == "washington") {
            return false
        }

        // 1. Verificar cada una de las partes de la cadena capital del país (separadas por "/")
        val partesCapital = pais.capital.split("/").map { normalizar(it) }
        for (parte in partesCapital) {
            if (parte == entradaNorm) {
                return true
            }
            // Si la capital se llama "Ciudad de X", permitir también responder únicamente "X" (ej. Panamá o México)
            if (parte.startsWith("ciudad de ")) {
                val x = parte.removePrefix("ciudad de ").trim()
                if (x.isNotEmpty() && x == entradaNorm) {
                    return true
                }
            }
        }

        // 2. Verificar las variantes adicionales registradas para este país
        val variantes = variantesPorCodigoPais[pais.codigo] ?: emptyList()
        for (v in variantes) {
            val vNorm = normalizar(v)
            if (vNorm == entradaNorm) {
                return true
            }
            // Si la variante se llama "Ciudad de X", permitir también responder únicamente "X"
            if (vNorm.startsWith("ciudad de ")) {
                val x = vNorm.removePrefix("ciudad de ").trim()
                if (x.isNotEmpty() && x == entradaNorm) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Si el país tiene más de una capital oficial (separadas por "/"),
     * retorna las capitales oficiales restantes que no fueron ingresadas por el usuario.
     */
    fun obtenerOtrasCapitales(pais: Pais, entradaUsuario: String): List<String> {
        val partes = pais.capital.split("/").map { it.trim() }
        if (partes.size <= 1) return emptyList()

        val entradaNorm = normalizar(entradaUsuario)

        return partes.filter { parte ->
            val pNorm = normalizar(parte)
            pNorm != entradaNorm
        }
    }
}
