package com.example.facturacionelunico.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "proveedor")
data class ProveedorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombreEmpresa: String,
    val nombreContacto: String,
    val telefono: String,
    val correo: String,
    val direccion: String
)

