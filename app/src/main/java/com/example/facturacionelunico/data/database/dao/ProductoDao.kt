package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert
    suspend fun insert(product: ProductoEntity)

    @Query("SELECT * FROM producto")
    fun getAll(): Flow<List<ProductoEntity>>
}
