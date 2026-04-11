package com.example.rutafix

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rutafix.models.Usuario
import com.example.rutafix.supabase.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class Registro : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro)

        val editNombre = findViewById<EditText>(R.id.editNombre)
        val editApellido = findViewById<EditText>(R.id.editApellido)
        val editEmail = findViewById<EditText>(R.id.editEmailRegistro)
        val editPass = findViewById<EditText>(R.id.editPasswordRegistro)
        val editConfirmPass = findViewById<EditText>(R.id.editConfirmPassword)
        val checkTerminos = findViewById<CheckBox>(R.id.checkTerminos)
        val btnRegistrar = findViewById<Button>(R.id.botonRegistrar)
        val btnBack = findViewById<ImageView>(R.id.btnBackToLogin)

        btnBack.setOnClickListener { finish() }

        btnRegistrar.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            val apellido = editApellido.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val pass = editPass.text.toString().trim()
            val confirmPass = editConfirmPass.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass != confirmPass) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass.length < 6) {
                Toast.makeText(this, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!checkTerminos.isChecked) {
                Toast.makeText(this, "Acepta los términos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarUsuario(nombre, apellido, email, pass)
        }
    }

    private fun registrarUsuario(nombre: String, apellido: String, email: String, pass: String) {
        lifecycleScope.launch {
            try {
                // PASO 1: Registro en Authentication
                Log.d("SUPABASE_DEBUG", "Iniciando Auth para: $email")
                val userInfo = SupabaseConfig.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = pass
                }

                val userId = userInfo?.id ?: SupabaseConfig.client.auth.currentUserOrNull()?.id
                Log.d("SUPABASE_DEBUG", "ID de usuario obtenido: $userId")

                if (userId != null) {
                    // PASO 2: Inserción en Tabla (Usar try-catch interno)
                    try {
                        val nuevoUsuario = Usuario(id = userId, nombres = nombre, apellidos = apellido)
                        Log.d("SUPABASE_DEBUG", "Insertando en DB: $nuevoUsuario")
                        
                        SupabaseConfig.client.postgrest["usuarios"].insert(nuevoUsuario)
                        
                        Log.d("SUPABASE_DEBUG", "¡Inserción exitosa!")
                        Toast.makeText(this@Registro, "¡Registro completado!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Registro, Login::class.java))
                        finish()
                    } catch (dbError: Exception) {
                        Log.e("SUPABASE_DEBUG", "Error al guardar en tabla 'usuarios': ${dbError.message}")
                        Toast.makeText(this@Registro, "Cuenta creada, pero hubo un error al guardar tus datos. Revisa el RLS.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("SUPABASE_DEBUG", "No se obtuvo ID tras el registro.")
                    Toast.makeText(this@Registro, "No se pudo obtener el ID del usuario.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("SUPABASE_DEBUG", "Error de Auth: ${e.message}")
                Toast.makeText(this@Registro, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}