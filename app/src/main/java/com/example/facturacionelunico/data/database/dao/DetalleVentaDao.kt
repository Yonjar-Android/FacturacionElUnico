package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.DetalleVentaEntity
import com.example.facturacionelunico.domain.models.ProductItem
import com.example.facturacionelunico.domain.models.invoice.DetailInvoiceProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleVentaDao {
    @Insert
    suspend fun insert(detalle: DetalleVentaEntity)

    @Query("SELECT * FROM detalle_venta")
    suspend fun getAll(): List<DetalleVentaEntity>

    @Query("""SELECT  
        p.id AS id,
        dv.cantidad AS quantity,
        dv.precio AS price,
        p.nombre AS name
FROM detalle_venta dv
        INNER JOIN producto p ON dv.idProducto = p.id
        WHERE idVenta = :id""")
    fun getDetailsByInvoiceId(id: Long): Flow<List<ProductItem>>
}
