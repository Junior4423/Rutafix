package com.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.rutafix.R

/**
 * Adaptador para el RecyclerView de la tienda RutaFix.
 * Implementa los tres métodos fundamentales explicados en clase por la profesora Angie.
 */
class TiendaAdapterRutaFix(private val listaProductos: List<ProductoRutaFix>) :
    RecyclerView.Adapter<TiendaAdapterRutaFix.ProductoViewHolder>() {

    /**
     * Infla el diseño XML (item_catalogo_producto) y crea el ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catalogo_producto, parent, false)
        return ProductoViewHolder(vista)
    }

    /**
     * Vincula los datos del producto con los elementos visuales del ViewHolder.
     */
    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaProductos[position]
        
        holder.imagen.setImageResource(producto.imagenProductoResId)
        holder.nombre.text = producto.nombreProducto
        holder.marca.text = producto.marcaProducto
        holder.precio.text = producto.precioProducto

        // Acción del botón según lo pedido (Toast simple por ahora)
        holder.btnAgregar.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Se agregó ${producto.nombreProducto} al carrito",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Devuelve la cantidad total de elementos en la lista (mínimo 4 productos).
     */
    override fun getItemCount(): Int = listaProductos.size

    /**
     * Clase interna que contiene las referencias a las vistas de cada item.
     * Uso de nombres de variables claros alineados con los IDs del layout.
     */
    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView = itemView.findViewById(R.id.iv_producto_imagen)
        val nombre: TextView = itemView.findViewById(R.id.tv_producto_nombre)
        val marca: TextView = itemView.findViewById(R.id.tv_producto_marca)
        val precio: TextView = itemView.findViewById(R.id.tv_producto_precio)
        val btnAgregar: AppCompatButton = itemView.findViewById(R.id.btn_producto_agregar)
    }
}
