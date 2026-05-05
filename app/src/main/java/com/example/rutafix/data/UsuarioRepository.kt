package com.example.rutafix.data

import com.example.rutafix.supabase.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object UsuarioRepository {

    @Serializable
    data class UsuarioData(
        val id: String,
        val nombres: String,
        val apellidos: String,
        val correo: String? = null,
        val rol: String = "cliente",
        val foto_url: String? = null
    )

    suspend fun existeUsuario(userId: String): Boolean {
        return try {
            val resultado = SupabaseConfig.client
                .postgrest["usuarios"]
                .select(Columns.raw("id")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeList<Map<String, String>>()
            resultado.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerUsuarioActual(): UsuarioData? {
        val userId = SupabaseConfig.client.auth
            .currentUserOrNull()?.id ?: return null
        return try {
            val resultado = SupabaseConfig.client
                .postgrest["usuarios"]
                .select {
                    filter { eq("id", userId) }
                }
                .decodeList<UsuarioData>()
            resultado.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun insertarUsuario(id: String, nombres: String, apellidos: String, correo: String) {
        SupabaseConfig.client.postgrest["usuarios"].insert(
            UsuarioData(id, nombres, apellidos, correo)
        )
    }

    suspend fun obtenerRolActual(): String {
        return try {
            val userId = SupabaseConfig.client.auth
                .currentUserOrNull()?.id ?: return "cliente"

            val resultado = SupabaseConfig.client
                .postgrest["usuarios"]
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeList<UsuarioData>()

            resultado.firstOrNull()?.rol ?: "cliente"
        } catch (e: Exception) {
            "cliente"
        }
    }

    // Regla 6: Actualiza ÚNICAMENTE nombres, apellidos y foto_url
    suspend fun actualizarPerfil(
        nombres: String,
        apellidos: String,
        fotoUrl: String? = null
    ) {
        val userId = SupabaseConfig.client.auth
            .currentUserOrNull()?.id ?: return

        // Usamos buildJsonObject para enviar un JSON parcial (PATCH) seguro
        val datos = buildJsonObject {
            put("nombres", nombres)
            put("apellidos", apellidos)
            if (fotoUrl != null) put("foto_url", fotoUrl)
        }

        SupabaseConfig.client.postgrest["usuarios"]
            .update(datos) {
                filter { eq("id", userId) }
            }
    }

    // Sube la foto al bucket avatars y devuelve la URL pública
    suspend fun subirFotoPerfil(
        contexto: android.content.Context,
        uri: android.net.Uri
    ): String {
        val userId = SupabaseConfig.client.auth
            .currentUserOrNull()?.id ?: return ""

        val bytes = if (uri.scheme == "content") {
            contexto.contentResolver
                .openInputStream(uri)?.readBytes()
        } else {
            java.io.File(uri.path!!).readBytes()
        } ?: return ""

        val rutaArchivo = "perfil_$userId.jpg"

        SupabaseConfig.client.storage["avatars"]
            .upload(
                path = rutaArchivo,
                data = bytes,
                options = { upsert = true }
            )

        return SupabaseConfig.client.storage["avatars"]
            .publicUrl(rutaArchivo)
    }
}
