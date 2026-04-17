package com.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.rutafix.Login
import com.example.rutafix.R
import com.example.rutafix.supabase.SupabaseConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.main.favorites.FavoritesFragment
import com.main.home.HomeFragment
import com.main.perfil.PerfilFragment
import com.main.products.ProductsFragment
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            reemplazarFragmento(HomeFragment(), "Inicio - RutaFix")
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_drawer_home -> reemplazarFragmento(HomeFragment(), "Inicio - RutaFix")
                R.id.nav_drawer_products -> reemplazarFragmento(ProductsFragment(), "Catálogo de Repuestos")
                R.id.nav_drawer_profile -> reemplazarFragmento(PerfilFragment(), "Mi Cuenta")
                R.id.nav_drawer_logout -> cerrarSesion()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_bottom_home -> { reemplazarFragmento(HomeFragment(), "Inicio"); true }
                R.id.nav_bottom_products -> { reemplazarFragmento(ProductsFragment(), "Tienda"); true }
                R.id.nav_bottom_favorites -> { reemplazarFragmento(FavoritesFragment(), "Mis Favoritos"); true }
                R.id.nav_bottom_profile -> { reemplazarFragmento(PerfilFragment(), "Mi Cuenta"); true }
                else -> false
            }
        }
    }

    private fun cerrarSesion() {
        lifecycleScope.launch {
            try {
                // 1. Cerrar sesión en servidor
                SupabaseConfig.client.auth.signOut()
                
                // NOTA: NO llamamos a limpiarCredenciales aquí para que la huella 
                // pueda usarse en el próximo inicio de sesión, tal como pide la guía.

                // 2. Redirigir al Login
                val intent = Intent(this@MainActivity, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error al salir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reemplazarFragmento(fragment: Fragment, titulo: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        findViewById<Toolbar>(R.id.toolbar)?.title = titulo
    }
}