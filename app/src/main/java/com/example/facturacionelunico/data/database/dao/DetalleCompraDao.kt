package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.DetalleCompraEntity
import com.example.facturacionelunico.domain.models.ProductItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleCompraDao {
    @Insert
    suspend fun insert(detalle: DetalleCompraEntity)

    @Update
    suspend fun update(detalle: DetalleCompraEntity)

    @Query("DELETE FROM detalle_compra WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM detalle_compra")
    suspend fun getAll(): List<DetalleCompraEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(detalles: List<DetalleCompraEntity>)

    @Query("DELETE FROM detalle_compra")
    suspend fun deleteAll()

    @Query("""SELECT 
        dc.id AS detailId,
        p.id AS id,
        dc.cantidad AS quantity,
        dc.precio AS price,
        p.nombre AS name,
        0 AS purchasePrice
FROM detalle_compra dc
        INNER JOIN producto p ON dc.idProducto = p.id
        WHERE dc.idCompra = :id""")
    fun getDetailsByPurchaseId(id: Long): Flow<List<ProductItem>>
}
