package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.DetalleVentaEntity

@Dao
interface DetalleVentaDao {
    @Insert
    suspend fun insert(detalle: DetalleVentaEntity)

    @Query("SELECT * FROM detalle_venta")
    suspend fun getAll(): List<DetalleVentaEntity>
}
