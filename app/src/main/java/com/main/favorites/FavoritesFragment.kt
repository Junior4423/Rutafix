package com.main.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.rutafix.R

/**
 * Fragmento genérico para la sección de Favoritos.
 */
class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_placeholder, container, false)
        
        vista.findViewById<TextView>(R.id.tv_placeholder_titulo).text = "Mis Favoritos"
        vista.findViewById<TextView>(R.id.tv_placeholder_mensaje).text = "Aquí podrás guardar los productos que más te interesan de RutaFix."
        
        return vista
    }
}
