package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.ClienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Insert
    suspend fun insert(cliente: ClienteEntity)

    @Query("SELECT * FROM cliente")
    fun getAll(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM cliente WHERE id = :id")
    fun getClientById(id: Long): Flow<ClienteEntity>
}

