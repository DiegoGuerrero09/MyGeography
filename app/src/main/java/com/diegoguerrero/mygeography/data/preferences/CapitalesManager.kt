package com.diegoguerrero.mygeography.data.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de persistencia para capitales modificadas manualmente por el usuario.
 */
class CapitalesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mygeography_capitales_custom", Context.MODE_PRIVATE)

    fun guardarCapital(codigo: String, nuevaCapital: String) {
        prefs.edit().putString(codigo.lowercase(), nuevaCapital.trim()).apply()
    }

    fun eliminarCapital(codigo: String) {
        prefs.edit().remove(codigo.lowercase()).apply()
    }

    fun obtenerTodas(): Map<String, String> {
        val resultado = mutableMapOf<String, String>()
        val todas = prefs.all
        for ((clave, valor) in todas) {
            if (valor is String && valor.isNotBlank()) {
                resultado[clave.lowercase()] = valor
            }
        }
        return resultado
    }

    fun tieneCapitalPersonalizada(codigo: String): Boolean {
        return prefs.contains(codigo.lowercase())
    }
}
