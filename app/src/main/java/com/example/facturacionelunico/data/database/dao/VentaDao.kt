package com.example.facturacionelunico.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.VentaEntity
import com.example.facturacionelunico.domain.models.invoice.InvoiceDetailLocalModel
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {
    @Insert
    suspend fun insert(venta: VentaEntity): Long

    @Update
    suspend fun update(venta: VentaEntity)

    @Query("""SELECT * FROM venta
        ORDER BY estado DESC
        """)
    fun getAll(): PagingSource<Int, VentaEntity>

    @Query("""SELECT * FROM venta
        WHERE id = :id
        """)
    suspend fun getInvoiceById(id: Long): VentaEntity

    @Query("""SELECT * FROM venta
        WHERE estado = 'PENDIENTE'
        ORDER BY estado DESC
        """)
    fun getInvoicesWithDebt(): PagingSource<Int, VentaEntity>

    @Query("""
    SELECT * FROM venta
    WHERE idCliente = :id
    ORDER BY 
        CASE WHEN estado = 'PENDIENTE' THEN 0 ELSE 1 END,
        fechaVenta DESC
""")
    suspend fun getInvoicesByClientId(id: Long): List<VentaEntity>

    @Query("""
    SELECT 
    v.id AS idFactura,
    c.nombre AS nombreCliente,
    c.apellido AS apellidoCliente,
    v.total AS totalFactura,
    IFNULL(SUM(a.totalPendiente), 0.0) AS totalPendiente
FROM venta v
LEFT JOIN cliente c ON v.idCliente = c.id
LEFT JOIN abono_venta a ON v.id = a.idVenta
WHERE v.id = :id
GROUP BY v.id, c.nombre, c.apellido, v.total
""") fun getInvoiceDetailById(id: Long): Flow<InvoiceDetailLocalModel>
}
