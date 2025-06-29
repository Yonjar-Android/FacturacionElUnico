package com.example.facturacionelunico.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.CategoriaEntity
import com.example.facturacionelunico.domain.models.DetailedProductModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Insert
    suspend fun insert(categoria: CategoriaEntity)

    // Verificar si una categoría con el mismo nombre ya existe
    @Query("SELECT * FROM categoria WHERE LOWER(nombre) = LOWER(:nombre) LIMIT 1")
    suspend fun existCategoryName(nombre: String): CategoriaEntity?

    @Update
    suspend fun update(categoria: CategoriaEntity)

    // Verificar si una categoría con el mismo nombre ya existe, excluyendo la categoría actual
    @Query("SELECT * FROM categoria WHERE LOWER(nombre) = LOWER(:nombre) AND id != :id LIMIT 1")
    suspend fun getOtherCategoryByName(nombre: String, id: Long): CategoriaEntity?

    @Query("SELECT * FROM categoria")
    fun getAll(): PagingSource<Int, CategoriaEntity>

    @Query("SELECT * FROM categoria")
    suspend fun getAllJson(): List<CategoriaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categorias: List<CategoriaEntity>)

    @Query("DELETE FROM categoria")
    suspend fun deleteAll()

    @Query("SELECT * FROM categoria WHERE id = :categoryId LIMIT 1")
    fun getCategoryById(categoryId: Long): Flow<CategoriaEntity>

    @Query("SELECT * FROM categoria WHERE nombre  LIKE '%' || :query || '%'")
    fun getCategoryByName(query: String): PagingSource<Int, CategoriaEntity>

    @Query("""
    SELECT producto.id, 
           producto.nombre as name,
           categoria.nombre as category,
           categoria.id as categoryId,
           marca.id as brandid,
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
