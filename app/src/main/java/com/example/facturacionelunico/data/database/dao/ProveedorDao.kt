package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.ProveedorEntity

@Dao
interface ProveedorDao {
    @Insert
    suspend fun insert(proveedor: ProveedorEntity)

    @Query("SELECT * FROM proveedor")
    suspend fun getAll(): List<ProveedorEntity>
}
