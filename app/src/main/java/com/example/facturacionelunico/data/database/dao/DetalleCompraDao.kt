package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.DetalleCompraEntity

@Dao
interface DetalleCompraDao {
    @Insert
    suspend fun insert(detalle: DetalleCompraEntity)

    @Query("SELECT * FROM detalle_compra")
    suspend fun getAll(): List<DetalleCompraEntity>
}
