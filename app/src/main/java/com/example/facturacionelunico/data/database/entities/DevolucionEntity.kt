package com.example.facturacionelunico.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "devolucion")
data class DevolucionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fechaDevolucion: Long,
    val idProveedor: Long
)

@Entity(
    tableName = "detalle_devolucion",
    foreignKeys = [
        ForeignKey(entity = DevolucionEntity::class, parentColumns = ["id"], childColumns = ["idDevolucion"]),
        ForeignKey(entity = ProductoEntity::class, parentColumns = ["id"], childColumns = ["idProducto"])
    ]
)
data class DetalleDevolucionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idDevolucion: Long,
    val idProducto: Long,
    val cantidad: Int,
    val motivo: String
)


