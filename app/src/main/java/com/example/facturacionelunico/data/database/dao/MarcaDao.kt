package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.MarcaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarcaDao {
    @Insert
    suspend fun insert(marca: MarcaEntity)

    @Query("SELECT * FROM marca")
    fun getAll(): Flow<List<MarcaEntity>>
}
