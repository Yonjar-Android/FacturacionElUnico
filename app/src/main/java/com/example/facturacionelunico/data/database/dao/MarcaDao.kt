package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.MarcaEntity

@Dao
interface MarcaDao {
    @Insert
    suspend fun insert(marca: MarcaEntity)

    @Query("SELECT * FROM marca")
    suspend fun getAll(): List<MarcaEntity>
}
