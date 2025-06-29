package com.example.facturacionelunico.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.ProductoEntity
import com.example.facturacionelunico.domain.models.DetailedProductModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert
    suspend fun insert(product: ProductoEntity)

    // Verificar si un producto con el mismo nombre ya existe
    @Query("SELECT * FROM producto WHERE LOWER(nombre) = LOWER(:nombre) LIMIT 1")
    suspend fun existProductName(nombre: String): ProductoEntity?

    @Update
    suspend fun update(product: ProductoEntity)

    // Verificar si un producto con el mismo nombre ya existe, excluyendo el producto actual
    @Query("SELECT * FROM producto WHERE LOWER(nombre) = LOWER(:nombre) AND id != :id LIMIT 1")
    suspend fun getOtherProductByName(nombre: String, id: Long): ProductoEntity?

    @Query("SELECT * FROM producto")
    fun getAll(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM producto")
    suspend fun getAllJson(): List<ProductoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos: List<ProductoEntity>)

    @Query("DELETE FROM producto")
    suspend fun deleteAll()

    @Query(
        """
    SELECT producto.id, 
           producto.nombre as name,
           COALESCE(Categoria.nombre, 'Sin categoria') as category,
           categoria.id as categoryId,
           COALESCE(marca.nombre, 'Sin marca') as brand,
           marca.id as brandId, 
           producto.precioVenta as salePrice,
           producto.precioCompra as purchasePrice,
           producto.stock,
           producto.descripcion as description,
           producto.foto as photo
    FROM producto
    LEFT JOIN categoria ON producto.idCategoria = categoria.id
    LEFT JOIN marca ON producto.idMarca = marca.id
"""
    )
    fun getAllDetailed(): PagingSource<Int, DetailedProductModel>

    @Query(
        """
    SELECT producto.id, 
           producto.nombre as name,
           COALESCE(Categoria.nombre, 'Sin categoria') as category,
           categoria.id as categoryId,
           COALESCE(marca.nombre, 'Sin marca') as brand,
           marca.id as brandId,  -- Añadido ID de marca
           producto.precioVenta as salePrice,
           producto.precioCompra as purchasePrice,
           producto.stock,
           producto.descripcion as description,
           producto.foto as photo
    FROM producto
    LEFT JOIN categoria ON producto.idCategoria = categoria.id
    LEFT JOIN marca ON producto.idMarca = marca.id
    WHERE producto.id = :idProduct
"""
    )
    suspend fun getDetailedById(idProduct: Long): DetailedProductModel?

    @Query(
        """
    SELECT producto.id, 
           producto.nombre as name,
           COALESCE(Categoria.nombre, 'Sin categoria') as category,
           categoria.id as categoryId,
           COALESCE(marca.nombre, 'Sin marca') as brand,
           marca.id as brandId,  -- Añadido ID de marca
           producto.precioVenta as salePrice,
           producto.precioCompra as purchasePrice,
           producto.stock,
           producto.descripcion as description,
           producto.foto as photo
    FROM producto
    LEFT JOIN categoria ON producto.idCategoria = categoria.id
    LEFT JOIN marca ON producto.idMarca = marca.id
    WHERE producto.nombre LIKE '%' || :query || '%'
"""
    )
    fun getProductsBySearch(query: String): PagingSource<Int, DetailedProductModel>

}
