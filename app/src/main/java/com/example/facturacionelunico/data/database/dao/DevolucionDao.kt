package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.DevolucionEntity

@Dao
interface DevolucionDao {
    @Insert
    suspend fun insert(devolucion: DevolucionEntity)

    @Query("SELECT * FROM devolucion")
    suspend fun getAll(): List<DevolucionEntity>
}
