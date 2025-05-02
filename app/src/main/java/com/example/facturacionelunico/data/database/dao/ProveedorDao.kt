package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.ProveedorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProveedorDao {
    @Insert
    suspend fun insert(proveedor: ProveedorEntity)

    @Query("SELECT * FROM proveedor")
    fun getAll(): Flow<List<ProveedorEntity>>

    @Query("SELECT * FROM proveedor WHERE id = :id")
    fun getSupplierById(id: Long): Flow<ProveedorEntity>
}
