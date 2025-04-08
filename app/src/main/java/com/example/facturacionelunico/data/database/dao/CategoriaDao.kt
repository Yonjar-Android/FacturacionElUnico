package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.CategoriaEntity

@Dao
interface CategoriaDao {
    @Insert
    suspend fun insert(categoria: CategoriaEntity)

    @Query("SELECT * FROM categoria")
    suspend fun getAll(): List<CategoriaEntity>
}
