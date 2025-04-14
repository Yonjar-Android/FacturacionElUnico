package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.MarcaEntity
import com.example.facturacionelunico.domain.models.DetailedProductModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MarcaDao {
    @Insert
    suspend fun insert(marca: MarcaEntity)

    @Update
    suspend fun update(marca: MarcaEntity)

    @Query("SELECT * FROM marca")
    fun getAll(): Flow<List<MarcaEntity>>

    @Query("SELECT * FROM marca WHERE id = :brandId LIMIT 1")
    fun getBrandById(brandId: Long): Flow<MarcaEntity>

    @Query("""
        SELECT * FROM marca
        WHERE nombre LIKE '%' || :query || '%'
    """)
    fun getBrandByName(query:String): Flow<List<MarcaEntity>>

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
