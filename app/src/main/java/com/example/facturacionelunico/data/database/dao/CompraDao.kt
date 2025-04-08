package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.CompraEntity

@Dao
interface CompraDao {
    @Insert
    suspend fun insert(compra: CompraEntity)

    @Query("SELECT * FROM compra")
    suspend fun getAll(): List<CompraEntity>
}
