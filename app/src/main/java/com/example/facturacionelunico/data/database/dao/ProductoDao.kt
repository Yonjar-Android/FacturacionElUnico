package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.ProductoEntity

@Dao
interface ProductoDao {
    @Insert
    suspend fun insert(producto: ProductoEntity)

    @Query("SELECT * FROM producto")
    suspend fun getAll(): List<ProductoEntity>
}
