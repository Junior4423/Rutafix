package com.main

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
import com.example.rutafix.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.main.favorites.FavoritesFragment
import com.main.home.HomeFragment
import com.main.perfil.PerfilFragment
import com.main.products.ProductsFragment

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

        // 1. Pantalla de Bienvenida al iniciar
        if (savedInstanceState == null) {
            reemplazarFragmento(HomeFragment(), "Inicio - RutaFix")
        }

        // --- Lógica Menú Lateral ---
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_drawer_home -> {
                    reemplazarFragmento(HomeFragment(), "Inicio - RutaFix")
                }
                R.id.nav_drawer_products -> {
                    reemplazarFragmento(ProductsFragment(), "Catálogo de Repuestos")
                }
                R.id.nav_drawer_services -> {
                    Toast.makeText(this, "Próximamente: Servicios de Asistencia en Ruta", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_drawer_profile -> {
                    reemplazarFragmento(PerfilFragment(), "Mi Cuenta")
                }
                R.id.nav_drawer_logout -> {
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // --- Lógica Menú Inferior ---
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_bottom_home -> {
                    reemplazarFragmento(HomeFragment(), "Inicio")
                    true
                }
                R.id.nav_bottom_products -> {
                    reemplazarFragmento(ProductsFragment(), "Tienda")
                    true
                }
                R.id.nav_bottom_favorites -> {
                    // Ahora redirige a un fragmento genérico de Favoritos
                    Toast.makeText(this, "Sección de favoritos próximamente", Toast.LENGTH_SHORT).show()
                    reemplazarFragmento(FavoritesFragment(), "Mis Favoritos")
                    true
                }
                R.id.nav_bottom_profile -> {
                    reemplazarFragmento(PerfilFragment(), "Mi Cuenta")
                    true
                }
                else -> false
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })
    }

    private fun reemplazarFragmento(fragment: Fragment, titulo: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        findViewById<Toolbar>(R.id.toolbar)?.title = titulo
    }
}