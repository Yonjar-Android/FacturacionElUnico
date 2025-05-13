package com.example.facturacionelunico.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "venta")
data class VentaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fechaVenta: Long,
    val total: Double,
    val idCliente: Long?,
    val estado: String, // PENDIENTE, COMPLETADO, CANCELADO
    val tipoPago: String // DEBITO, CREDITO
)

@Entity(
    tableName = "detalle_venta",
    foreignKeys = [
        ForeignKey(
            entity = VentaEntity::class,
            parentColumns = ["id"],  // <- Esto debe ser string literal, como está aquí
            childColumns = ["idVenta"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["id"],
            childColumns = ["idProducto"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["idVenta"]),
        Index(value = ["idProducto"])
    ]
)
data class DetalleVentaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idVenta: Long,
    val idProducto: Long,
    val cantidad: Int,
    val precio: Double,
    val subtotal: Double,
    val fechaActualizacion: Long
)



