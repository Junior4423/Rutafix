package com.main.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.rutafix.R

class PerfilFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_placeholder, container, false)
        vista.findViewById<TextView>(R.id.tv_placeholder_titulo).text = "Mi Perfil"
        vista.findViewById<TextView>(R.id.tv_placeholder_mensaje).text = "Aquí podrás editar tus datos de usuario de RutaFix."
        return vista
    }
}