package com.main.perfil

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.rutafix.R
import com.example.rutafix.models.Usuario
import com.example.rutafix.supabase.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class PerfilFragment : Fragment() {

    private lateinit var editNombres: EditText
    private lateinit var editApellidos: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPass: EditText
    private lateinit var editConfirmPass: EditText
    private lateinit var btnGuardar: Button
    private lateinit var progress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        editNombres = view.findViewById(R.id.editNombresPerfil)
        editApellidos = view.findViewById(R.id.editApellidosPerfil)
        editEmail = view.findViewById(R.id.editEmailPerfil)
        editPass = view.findViewById(R.id.editPassPerfil)
        editConfirmPass = view.findViewById(R.id.editConfirmPassPerfil)
        btnGuardar = view.findViewById(R.id.btnGuardarCambios)
        progress = view.findViewById(R.id.progressPerfil)

        cargarDatosUsuario()

        btnGuardar.setOnClickListener {
            validarYActualizar()
        }

        return view
    }

    private fun cargarDatosUsuario() {
        val user = SupabaseConfig.client.auth.currentUserOrNull() ?: return
        editEmail.setText(user.email)

        lifecycleScope.launch {
            try {
                val usuarioData = SupabaseConfig.client.postgrest["usuarios"]
                    .select(columns = Columns.ALL) {
                        filter { eq("id", user.id) } // Cambio de 'identificacion' a 'id'
                    }.decodeSingle<Usuario>()

                editNombres.setText(usuarioData.nombres)
                editApellidos.setText(usuarioData.apellidos)
            } catch (e: Exception) {
                Log.e("Supabase", "Error carga: ${e.message}")
            }
        }
    }

    private fun validarYActualizar() {
        val nombres = editNombres.text.toString().trim()
        val apellidos = editApellidos.text.toString().trim()
        val email = editEmail.text.toString().trim()
        val pass = editPass.text.toString().trim()
        val confirmPass = editConfirmPass.text.toString().trim()

        if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty()) {
            Toast.makeText(context, getString(R.string.campos_obligatorios_vacios), Toast.LENGTH_SHORT).show()
            return
        }

        if (pass.isNotEmpty()) {
            if (pass.length < 6) {
                Toast.makeText(context, getString(R.string.error_clave_corta), Toast.LENGTH_SHORT).show()
                return
            }
            if (pass != confirmPass) {
                Toast.makeText(context, getString(R.string.claves_no_coinciden), Toast.LENGTH_SHORT).show()
                return
            }
        }

        actualizarPerfil(nombres, apellidos, email, pass)
    }

    private fun actualizarPerfil(nombres: String, apellidos: String, email: String, pass: String) {
        val userId = SupabaseConfig.client.auth.currentUserOrNull()?.id ?: return
        
        lifecycleScope.launch {
            try {
                progress.visibility = View.VISIBLE
                btnGuardar.isEnabled = false

                SupabaseConfig.client.postgrest["usuarios"].update({
                    set("nombres", nombres)
                    set("apellidos", apellidos)
                }) {
                    filter { eq("id", userId) } // Cambio de 'identificacion' a 'id'
                }

                SupabaseConfig.client.auth.updateUser {
                    this.email = email
                    if (pass.isNotEmpty()) this.password = pass
                }

                Toast.makeText(context, getString(R.string.perfil_actualizado_exito), Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                progress.visibility = View.GONE
                btnGuardar.isEnabled = true
            }
        }
    }
}