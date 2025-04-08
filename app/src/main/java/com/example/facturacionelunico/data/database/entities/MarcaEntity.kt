package com.example.facturacionelunico.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marca")
data class MarcaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String
)

