package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.DetalleDevolucionEntity

@Dao
interface DetalleDevolucionDao {
    @Insert
    suspend fun insert(detalle: DetalleDevolucionEntity)

    @Query("SELECT * FROM detalle_devolucion")
    suspend fun getAll(): List<DetalleDevolucionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(detalles: List<DetalleDevolucionEntity>)

    @Query("DELETE FROM detalle_devolucion")
    suspend fun deleteAll()
}
