package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.CategoriaEntity
import com.example.facturacionelunico.domain.models.DetailedProductModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Insert
    suspend fun insert(categoria: CategoriaEntity)

    @Update
    suspend fun update(categoria: CategoriaEntity)

    @Query("SELECT * FROM categoria")
    fun getAll(): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categoria WHERE id = :categoryId LIMIT 1")
    fun getCategoryById(categoryId: Long): Flow<CategoriaEntity>

    @Query("""
    SELECT producto.id, 
           producto.nombre as name,
           categoria.nombre as category,
           COALESCE(marca.nombre, 'Sin marca') as brand,
           producto.precioVenta as salePrice,
           producto.precioCompra as purchasePrice,
           producto.stock,
           producto.descripcion as description,
           producto.foto as photo
    FROM producto
    INNER JOIN categoria ON producto.idCategoria = categoria.id
    LEFT JOIN marca ON producto.idMarca = marca.id
    WHERE producto.idCategoria = :categoryId
""")
    suspend fun getDetailedByCategoryId(categoryId: Long): List<DetailedProductModel>
}
