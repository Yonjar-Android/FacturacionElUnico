package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.CategoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Insert
    suspend fun insert(categoria: CategoriaEntity)

    @Query("SELECT * FROM categoria")
    fun getAll(): Flow<List<CategoriaEntity>>
}
