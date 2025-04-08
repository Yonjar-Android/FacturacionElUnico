package com.example.facturacionelunico.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "producto")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val idMarca: Long,
    val idCategoria: Long,
    val descripcion: String,
    val foto: String?,
    val stock: Int,
    val precioVenta: Double,
    val precioCompra: Double
)
