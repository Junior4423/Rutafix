package com.example.rutafix.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.android.Android

object SupabaseConfig {
    
    private const val SUPABASE_URL = "https://livxjniasxpiydiwmcbm.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imxpdnhqbmlhc3hwaXlkaXdtY2JtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU1MTYxNjUsImV4cCI6MjA5MTA5MjE2NX0.NLF-EY3pHn-6yitHkLdZ6QD9Ccs_DUQGki8GKKhbx2Q"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            // Con Ktor 3.0, el motor se configura de esta manera
            httpEngine = Android.create()

            install(Auth)
            install(Postgrest)
        }
    }
}