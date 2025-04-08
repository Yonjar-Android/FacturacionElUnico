package com.example.facturacionelunico.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "abono_compra")
data class AbonoCompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idCompra: Long,
    val fechaCreacion: Long,
    val totalAPagar: Double,
    val totalPendiente: Double
)

@Entity(
    tableName = "detalle_abono_compra",
    foreignKeys = [
        ForeignKey(entity = AbonoCompraEntity::class, parentColumns = ["id"], childColumns = ["idAbonoCompra"])
    ]
)
data class DetalleAbonoCompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idAbonoCompra: Long,
    val monto: Double,
    val fechaAbono: Long
)

