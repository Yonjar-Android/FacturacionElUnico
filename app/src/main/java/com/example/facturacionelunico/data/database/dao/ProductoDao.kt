package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.ProductoEntity
import com.example.facturacionelunico.domain.models.DetailedProductModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert
    suspend fun insert(product: ProductoEntity)

    @Update
    suspend fun update(product: ProductoEntity)

    @Query("SELECT * FROM producto")
    fun getAll(): Flow<List<ProductoEntity>>

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
    LEFT JOIN marca ON producto.idMarca = marca.id  -- Cambiado a LEFT JOIN
""")
    fun getAllDetailed(): Flow<List<DetailedProductModel>>

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
    WHERE producto.id = :idProduct
""")
    suspend fun getDetailedById(idProduct: Long): DetailedProductModel?
}
