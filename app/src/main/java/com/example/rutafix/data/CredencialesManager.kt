package com.example.rutafix.data

import android.content.Context
import android.content.SharedPreferences

object CredencialesManager {
    private const val PREFS_NAME = "auth"
    private const val KEY_CORREO = "correo"
    private const val KEY_CONTRASENA = "contrasena"
    private const val KEY_HUELLA = "huella_activa"
    private const val KEY_ROL = "rol"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun guardarCredenciales(context: Context, correo: String, contrasena: String, rol: String? = null) {
        getPrefs(context).edit().apply {
            putString(KEY_CORREO, correo)
            putString(KEY_CONTRASENA, contrasena)
            putBoolean(KEY_HUELLA, true)
            rol?.let { putString(KEY_ROL, it) }
            apply()
        }
    }

    fun guardarRol(context: Context, rol: String) {
        getPrefs(context).edit().putString(KEY_ROL, rol).apply()
    }

    fun obtenerRol(context: Context): String? =
        getPrefs(context).getString(KEY_ROL, "cliente")

    fun limpiarCredenciales(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    fun huellaActiva(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_HUELLA, false)

    fun obtenerCorreo(context: Context): String? =
        getPrefs(context).getString(KEY_CORREO, null)

    fun obtenerContrasena(context: Context): String? =
        getPrefs(context).getString(KEY_CONTRASENA, null)
}
