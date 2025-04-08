package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.VentaEntity

@Dao
interface VentaDao {
    @Insert
    suspend fun insert(venta: VentaEntity)

    @Query("SELECT * FROM venta")
    suspend fun getAll(): List<VentaEntity>
}
