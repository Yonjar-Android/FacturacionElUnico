package com.example.facturacionelunico.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.MarcaEntity
import com.example.facturacionelunico.domain.models.DetailedProductModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MarcaDao {
    @Insert
    suspend fun insert(marca: MarcaEntity)

    // Verificar si una marca con el mismo nombre ya existe
    @Query("SELECT * FROM marca WHERE LOWER(nombre) = LOWER(:nombre) LIMIT 1")
    suspend fun existBrandName(nombre: String): MarcaEntity?

    @Update
    suspend fun update(marca: MarcaEntity)

    // Verificar si una marca con el mismo nombre ya existe, excluyendo la marca actual
    @Query("SELECT * FROM marca WHERE LOWER(nombre) = LOWER(:nombre) AND id != :id LIMIT 1")
    suspend fun getOtherBrandByName(nombre: String, id: Long): MarcaEntity?

    @Query("SELECT * FROM marca")
    fun getAll(): PagingSource<Int, MarcaEntity>

    @Query("SELECT * FROM marca")
    suspend fun getAllJson(): List<MarcaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(marcas: List<MarcaEntity>)

    @Query("DELETE FROM marca")
    suspend fun deleteAll()

    @Query("SELECT * FROM marca WHERE id = :brandId LIMIT 1")
    fun getBrandById(brandId: Long): Flow<MarcaEntity>

    @Query("""
        SELECT * FROM marca
        WHERE nombre LIKE '%' || :query || '%'
    """)
    fun getBrandByName(query:String): PagingSource<Int, MarcaEntity>

    // Obtener productos con detalles de marca
    @Query("""
    SELECT producto.id, 
           producto.nombre as name,
           categoria.nombre as category,
           COALESCE(marca.nombre, 'Sin marca') as brand,
           categoria.id as categoryId,
           marca.id as brandid,
           producto.precioVenta as salePrice,
           producto.precioCompra as purchasePrice,
           producto.stock,
           producto.descripcion as description,
           producto.foto as photo
    FROM producto
    INNER JOIN categoria ON producto.idCategoria = categoria.id
    LEFT JOIN marca ON producto.idMarca = marca.id
    WHERE producto.idMarca = :brandId
""")
    suspend fun getDetailedByBrandId(brandId: Long): List<DetailedProductModel>
}
