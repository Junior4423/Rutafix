package com.example.rutafix

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rutafix.supabase.SupabaseConfig
import com.main.MainActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val editEmail = findViewById<EditText>(R.id.textEmail)
        val editPass = findViewById<EditText>(R.id.textPassword)
        val botonIngresa = findViewById<Button>(R.id.botonIngresa)
        val textRegistrateLink = findViewById<TextView>(R.id.textRegistrateLink)

        botonIngresa.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val pass = editPass.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUsuario(email, pass)
        }

        textRegistrateLink.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }
    }

    private fun loginUsuario(email: String, pass: String) {
        lifecycleScope.launch {
            try {
                // Validación real con Supabase
                SupabaseConfig.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = pass
                }

                // Si no lanza excepción, el login es correcto
                Toast.makeText(this@Login, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@Login, MainActivity::class.java))
                finish()

            } catch (e: Exception) {
                // Si falla (usuario no existe o clave errónea) entra aquí
                Toast.makeText(this@Login, "Credenciales incorrectas o usuario no existe", Toast.LENGTH_LONG).show()
            }
        }
    }
}