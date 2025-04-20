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
""")
    fun getAllDetailed(): Flow<List<DetailedProductModel>>

    @Query("""
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
""")
    suspend fun getDetailedById(idProduct: Long): DetailedProductModel?

    @Query("""
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
""")
    fun getProductsBySearch(query: String): Flow<List<DetailedProductModel>>

}
