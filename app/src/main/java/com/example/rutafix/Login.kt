package com.example.rutafix

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.rutafix.data.CredencialesManager
import com.example.rutafix.supabase.SupabaseConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.main.MainActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {

    private lateinit var tvHuella: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val editEmail = findViewById<EditText>(R.id.textEmail)
        val editPass = findViewById<EditText>(R.id.textPassword)
        val botonIngresa = findViewById<Button>(R.id.botonIngresa)
        val btnGoogle = findViewById<LinearLayout>(R.id.btnGoogle)
        val textRegistrateLink = findViewById<TextView>(R.id.textRegistrateLink)
        
        // Referencia al ID de la guía
        tvHuella = findViewById(R.id.in_huella)

        configurarVisibilidadHuella()

        tvHuella.setOnClickListener { mostrarDialogoHuella() }

        // --- Configuración de Google ---
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("69192934399-e6dkm8apm5citvc9gqol4ric7g1cplo1.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    loginConGoogleEnSupabase(idToken)
                }
            } catch (e: Exception) {
                Log.e("GoogleAuth", "Error: ${e.message}")
                Toast.makeText(this, "Error al conectar con Google", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoogle.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                googleLauncher.launch(googleSignInClient.signInIntent)
            }
        }

        botonIngresa.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val pass = editPass.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()) loginUsuario(email, pass)
        }

        textRegistrateLink.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        configurarVisibilidadHuella()
    }

    private fun loginUsuario(email: String, pass: String) {
        lifecycleScope.launch {
            try {
                SupabaseConfig.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = pass
                }
                
                // PASO 3.4 de la guía: Guardar credenciales
                CredencialesManager.guardarCredenciales(this@Login, email, pass)
                
                irAPantallaPrincipal()
            } catch (e: Exception) {
                Toast.makeText(this@Login, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginConGoogleEnSupabase(idToken: String) {
        lifecycleScope.launch {
            try {
                SupabaseConfig.client.auth.signInWith(IDToken) {
                    this.idToken = idToken
                    this.provider = Google
                }
                irAPantallaPrincipal()
            } catch (e: Exception) {
                Toast.makeText(this@Login, "Error Supabase: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // PASO 3.6 de la guía
    private fun configurarVisibilidadHuella() {
        val huellaActiva = CredencialesManager.huellaActiva(this)
        val biometricManager = BiometricManager.from(this)
        val biometriaDisponible = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS

        tvHuella.visibility = if (huellaActiva && biometriaDisponible) View.VISIBLE else View.GONE
    }

    // PASO 3.7 de la guía
    private fun mostrarDialogoHuella() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                val correo = CredencialesManager.obtenerCorreo(this@Login)
                val contrasena = CredencialesManager.obtenerContrasena(this@Login)
                
                if (correo != null && contrasena != null) {
                    lifecycleScope.launch {
                        try {
                            SupabaseConfig.client.auth.signInWith(Email) {
                                this.email = correo
                                this.password = contrasena
                            }
                            irAPantallaPrincipal()
                        } catch (e: Exception) {
                            Toast.makeText(this@Login, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this@Login, "Sesión expirada. Inicia sesión con tu correo.", Toast.LENGTH_LONG).show()
                    CredencialesManager.limpiarCredenciales(this@Login)
                    configurarVisibilidadHuella()
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && 
                    errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(this@Login, "Error biométrico: $errString", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(this@Login, "Huella no reconocida, intenta de nuevo", Toast.LENGTH_SHORT).show()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso con huella")
            .setSubtitle("Usa tu huella dactilar para ingresar")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    // PASO 3.5 de la guía
    private fun irAPantallaPrincipal() {
        runOnUiThread {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}