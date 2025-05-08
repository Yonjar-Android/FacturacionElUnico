package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.AbonoVentaEntity

@Dao
interface AbonoVentaDao {
    @Insert
    suspend fun insert(abono: AbonoVentaEntity):Long

    @Query("SELECT * FROM abono_venta")
    suspend fun getAll(): List<AbonoVentaEntity>
}
