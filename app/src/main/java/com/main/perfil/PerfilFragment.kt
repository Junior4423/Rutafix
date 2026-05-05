package com.main.perfil

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.rutafix.R
import com.example.rutafix.data.UsuarioRepository
import com.example.rutafix.supabase.SupabaseConfig
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import java.io.File

class PerfilFragment : Fragment() {

    private var uriFotoSeleccionada: Uri? = null
    private lateinit var ivPerfilFoto: ImageView
    private lateinit var archivoFotoTemp: File
    
    // UI Elements
    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etRol: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etReContrasena: EditText
    private lateinit var btnGuardar: Button
    private lateinit var progressBar: ProgressBar

    // Lanzador para solicitar el permiso de cámara
    private val lanzadorPermisoCamara = registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) abrirCamara()
        else Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    // Lanzador para la cámara
    private val lanzadorCamara = registerForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) {
            uriFotoSeleccionada = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                archivoFotoTemp
            )
            cargarImagen(uriFotoSeleccionada)
        }
    }

    // Lanzador para la galería
    private val lanzadorGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uriFotoSeleccionada = it
            cargarImagen(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas (basado en el XML actualizado)
        ivPerfilFoto = view.findViewById(R.id.iv_perfil_foto)
        val ivCamaraIcon = view.findViewById<ImageView>(R.id.iv_camara_icon)
        etNombres = view.findViewById(R.id.et_nombres)
        etApellidos = view.findViewById(R.id.et_apellidos)
        etCorreo = view.findViewById(R.id.et_correo)
        etRol = view.findViewById(R.id.et_rol)
        etContrasena = view.findViewById(R.id.et_contrasena)
        etReContrasena = view.findViewById(R.id.et_recontrasena)
        btnGuardar = view.findViewById(R.id.btn_guardar)
        progressBar = view.findViewById(R.id.progress_perfil)

        cargarDatosActuales()

        ivCamaraIcon.setOnClickListener { mostrarOpcionesFoto() }
        btnGuardar.setOnClickListener { validarYGuardar() }
    }

    private fun cargarImagen(uri: Any?) {
        ivPerfilFoto.load(uri) {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.ic_launcher_foreground)
        }
    }

    private fun cargarDatosActuales() {
        lifecycleScope.launch {
            val usuario = UsuarioRepository.obtenerUsuarioActual()
            usuario?.let {
                etNombres.setText(it.nombres)
                etApellidos.setText(it.apellidos)
                etCorreo.setText(it.correo ?: SupabaseConfig.client.auth.currentUserOrNull()?.email)
                etRol.setText(it.rol)
                
                if (!it.foto_url.isNullOrEmpty()) {
                    cargarImagen(it.foto_url)
                }
            }
        }
    }

    private fun mostrarOpcionesFoto() {
        val opciones = arrayOf("Tomar foto", "Elegir de galería")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Foto de perfil")
            .setItems(opciones) { _, cual ->
                when (cual) {
                    0 -> verificarPermisoCamara()
                    1 -> lanzadorGaleria.launch("image/*")
                }
            }
            .show()
    }

    private fun verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara()
        } else {
            lanzadorPermisoCamara.launch(Manifest.permission.CAMERA)
        }
    }

    private fun abrirCamara() {
        val carpeta = File(requireContext().cacheDir, "images")
        carpeta.mkdirs()
        archivoFotoTemp = File(carpeta, "foto_perfil_temp.jpg")
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", archivoFotoTemp)
        lanzadorCamara.launch(uri)
    }

    private fun validarYGuardar() {
        val nombres = etNombres.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val pass = etContrasena.text.toString()
        val rePass = etReContrasena.text.toString()

        if (nombres.isEmpty() || apellidos.isEmpty()) {
            Toast.makeText(requireContext(), "Nombres y apellidos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass.isNotEmpty()) {
            if (pass.length < 6) {
                Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return
            }
            if (pass != rePass) {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return
            }
        }

        guardarCambios(nombres, apellidos, pass)
    }

    private fun guardarCambios(nombres: String, apellidos: String, contrasena: String) {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                btnGuardar.isEnabled = false

                var fotoUrl: String? = null
                if (uriFotoSeleccionada != null) {
                    fotoUrl = UsuarioRepository.subirFotoPerfil(requireContext(), uriFotoSeleccionada!!)
                }

                // Regla 6: Update parcial (nombres, apellidos, foto_url)
                UsuarioRepository.actualizarPerfil(nombres, apellidos, fotoUrl)

                if (contrasena.isNotEmpty()) {
                    SupabaseConfig.client.auth.updateUser { password = contrasena }
                }

                Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progressBar.visibility = View.GONE
                btnGuardar.isEnabled = true
            }
        }
    }
}
