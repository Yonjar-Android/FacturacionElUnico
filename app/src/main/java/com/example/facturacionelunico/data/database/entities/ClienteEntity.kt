package com.example.facturacionelunico.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cliente",
    indices = [Index(value = ["identificadorCliente"], unique = true)]
)
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val apellido: String,
    val telefono: String,
    val identificadorCliente: Int // o String si prefieres usar letras también
)
