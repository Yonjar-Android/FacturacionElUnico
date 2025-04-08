package com.example.facturacionelunico.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "abono_venta")
data class AbonoVentaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idVenta: Long,
    val fechaCreacion: Long,
    val totalAPagar: Double,
    val totalPendiente: Double
)

@Entity(
    tableName = "detalle_abono_venta",
    foreignKeys = [
        ForeignKey(entity = AbonoVentaEntity::class, parentColumns = ["id"], childColumns = ["idAbonoVenta"])
    ]
)
data class DetalleAbonoVentaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idAbonoVenta: Long,
    val monto: Double,
    val fechaAbono: Long
)
