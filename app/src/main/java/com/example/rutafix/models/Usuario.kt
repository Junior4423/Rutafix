package com.example.rutafix.models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String, // Coincide con la columna 'id' de tu SQL
    val nombres: String,
    val apellidos: String
)