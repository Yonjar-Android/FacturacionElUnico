package com.example.facturacionelunico.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "compra")
data class CompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fechaCompra: Long,
    val total: Double,
    val idProveedor: Long,
    val estado: String // PENDIENTE, COMPLETADO, CANCELADO
)

@Entity(
    tableName = "detalle_compra",
    foreignKeys = [
        ForeignKey(entity = CompraEntity::class, parentColumns = ["id"], childColumns = ["idCompra"]),
        ForeignKey(entity = ProductoEntity::class, parentColumns = ["id"], childColumns = ["idProducto"])
    ]
)
data class DetalleCompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idCompra: Long,
    val idProducto: Long,
    val cantidad: Int,
    val precio: Double,
    val subtotal: Double
)


