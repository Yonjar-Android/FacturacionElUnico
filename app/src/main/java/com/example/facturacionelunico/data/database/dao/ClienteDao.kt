package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.ClienteEntity

@Dao
interface ClienteDao {
    @Insert
    suspend fun insert(cliente: ClienteEntity)

    @Query("SELECT * FROM cliente")
    suspend fun getAll(): List<ClienteEntity>
}

