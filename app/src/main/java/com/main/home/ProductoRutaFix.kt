package com.main.home

import com.example.rutafix.R

/**
 * Modelo de datos para representar un producto de la tienda RutaFix.
 * Se utilizan nombres de propiedades descriptivos para evitar ambigüedades.
 */
data class ProductoRutaFix(
    val idProducto: Int,
    val nombreProducto: String,
    val marcaProducto: String,
    val descripcionProducto: String,
    val precioProducto: String,
    val imagenProductoResId: Int
)

/**
 * Proveedor de datos estáticos para la Actividad #2.
 * Contiene la lista de los 4 productos automotrices requeridos.
 */
object ProveedorProductosRutaFix {
    fun obtenerListaProductos(): List<ProductoRutaFix> {
        return listOf(
            ProductoRutaFix(
                idProducto = 1,
                nombreProducto = "Aceite Mobil 25W-50 Alto Kilometraje",
                marcaProducto = "Mobil",
                descripcionProducto = "Aceite mineral para motores con más de 150,000 km. Presentación: Cuarto.",
                precioProducto = "30.000 COP",
                imagenProductoResId = R.drawable.aceite_20w50 // Asegúrate de renombrar el archivo a aceite_20w50 en drawable
            ),
            ProductoRutaFix(
                idProducto = 2,
                nombreProducto = "Juego De Herramientas Pretul 83 Piezas",
                marcaProducto = "Pretul",
                descripcionProducto = "Set completo con copas y puntas para mantenimiento de carro y moto.",
                precioProducto = "100.000 COP",
                imagenProductoResId = R.drawable.juego_de_herramientas // Asegúrate de renombrar el archivo a juego_de_herramientas en drawable
            ),
            ProductoRutaFix(
                idProducto = 3,
                nombreProducto = "Cargador de Batería Inteligente 12V",
                marcaProducto = "RTIanye",
                descripcionProducto = "Cargador con pantalla LCD y reparación por pulsos para baterías de 12V.",
                precioProducto = "100.000 COP",
                imagenProductoResId = R.drawable.cargador_bateria // Asegúrate de renombrar el archivo a cargador_bateria en drawable
            ),
            ProductoRutaFix(
                idProducto = 4,
                nombreProducto = "Inflador De Llantas Portátil 150 PSI",
                marcaProducto = "Genérico",
                descripcionProducto = "Compresor inalámbrico multifunción con batería de 4000mAh y luz LED.",
                precioProducto = "70.000 COP",
                imagenProductoResId = R.drawable.inflador_de_llantas // Asegúrate de renombrar el archivo a inflador_de_llantas en drawable
            )
        )
    }
}
