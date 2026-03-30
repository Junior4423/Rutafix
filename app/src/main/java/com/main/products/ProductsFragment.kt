package com.main.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rutafix.R
import com.main.home.ProveedorProductosRutaFix
import com.main.home.TiendaAdapterRutaFix

/**
 * Fragmento que muestra el Catálogo de Productos (RecyclerView).
 */
class ProductsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout que contiene el RecyclerView
        val vista = inflater.inflate(R.layout.fragment_home_tienda, container, false)

        // Configuración del RecyclerView
        val rvCatalogo = vista.findViewById<RecyclerView>(R.id.rv_home_catalogo_automotriz)
        rvCatalogo.layoutManager = LinearLayoutManager(requireContext())

        // Carga de productos y adaptador
        val listaProductos = ProveedorProductosRutaFix.obtenerListaProductos()
        rvCatalogo.adapter = TiendaAdapterRutaFix(listaProductos)

        return vista
    }
}
