package com.diegoguerrero.mygeography.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.diegoguerrero.mygeography.data.model.RegionQuiz
import com.diegoguerrero.mygeography.data.model.TipoQuiz

class EstadisticasManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mygeography_stats", Context.MODE_PRIVATE)

    fun obtenerMejorPorcentaje(tipo: TipoQuiz, region: RegionQuiz): Int {
        val clave = "mejor_${tipo.name.lowercase()}_${region.id}"
        return prefs.getInt(clave, -1)
    }

    fun guardarPorcentaje(tipo: TipoQuiz, region: RegionQuiz, porcentaje: Int) {
        val clave = "mejor_${tipo.name.lowercase()}_${region.id}"
        val actual = prefs.getInt(clave, -1)
        if (porcentaje > actual) {
            prefs.edit().putInt(clave, porcentaje).apply()
        }
    }
}
