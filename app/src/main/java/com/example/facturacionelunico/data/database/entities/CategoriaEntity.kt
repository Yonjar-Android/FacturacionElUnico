package com.example.facturacionelunico.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categoria")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String
)
