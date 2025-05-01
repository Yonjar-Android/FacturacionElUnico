package com.example.facturacionelunico.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cliente")
data class ClienteEntity(
@PrimaryKey val id: Long = 0,
val nombre: String,
val apellido: String,
val telefono: String
)
