package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.DetalleAbonoCompraEntity

@Dao
interface DetalleAbonoCompraDao {
    @Insert
    suspend fun insert(detalle: DetalleAbonoCompraEntity)

    @Query("SELECT * FROM detalle_abono_compra")
    suspend fun getAll(): List<DetalleAbonoCompraEntity>
}
