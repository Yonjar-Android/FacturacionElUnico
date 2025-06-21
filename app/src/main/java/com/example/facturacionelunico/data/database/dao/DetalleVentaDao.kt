package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.DetalleVentaEntity
import com.example.facturacionelunico.domain.models.ProductItem
import com.example.facturacionelunico.domain.models.reports.ReporteMensualDto
import com.example.facturacionelunico.domain.models.reports.ReporteMensualRaw
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleVentaDao {
    @Insert
    suspend fun insert(detalle: DetalleVentaEntity)

    @Update
    suspend fun update(detalle: DetalleVentaEntity)

    @Query("DELETE FROM detalle_venta WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM detalle_venta")
    suspend fun getAll(): List<DetalleVentaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(detalles: List<DetalleVentaEntity>)

    @Query("DELETE FROM detalle_venta")
    suspend fun deleteAll()

    @Query("SELECT * FROM detalle_venta WHERE id = :id")
    suspend fun getByInvoiceDetailId(id: Long): DetalleVentaEntity

    @Query("""SELECT  
        dv.id AS detailId,
        p.id AS id,
        dv.cantidad AS quantity,
        dv.precio AS price,
        p.nombre AS name,
        dv.precioCompra AS purchasePrice
FROM detalle_venta dv
        INNER JOIN producto p ON dv.idProducto = p.id
        WHERE idVenta = :id""")
    fun getDetailsByInvoiceId(id: Long): Flow<List<ProductItem>>

    @Query("""
        SELECT 
            SUM(subtotal) AS totalVendido,
            SUM((precio - precioCompra) * cantidad) AS gananciaNeta
        FROM detalle_venta
        INNER JOIN venta ON venta.id = detalle_venta.idVenta
        WHERE strftime('%m', datetime(fechaVenta / 1000, 'unixepoch')) = :mes
          AND strftime('%Y', datetime(fechaVenta / 1000, 'unixepoch')) = :anio
          
    """)
    suspend fun getReporteMensual(mes: String, anio: String): ReporteMensualDto

    //AND venta.estado = 'COMPLETADO'

    @Query("""
        SELECT 
            strftime('%m', datetime(venta.fechaVenta / 1000, 'unixepoch')) AS mes,
            strftime('%Y', datetime(venta.fechaVenta / 1000, 'unixepoch')) AS anio,
            SUM(detalle_venta.subtotal) AS totalVendido,
            SUM((detalle_venta.precio - detalle_venta.precioCompra) * detalle_venta.cantidad) AS gananciaNeta
        FROM detalle_venta
        INNER JOIN venta ON venta.id = detalle_venta.idVenta
        WHERE strftime('%Y', datetime(venta.fechaVenta / 1000, 'unixepoch')) = :anio
        GROUP BY mes
        ORDER BY mes ASC
    """)
    suspend fun getResumenPorAnio(anio: String): List<ReporteMensualRaw>

    //AND venta.estado = 'COMPLETADO'

    @Query(
        """
    SELECT DISTINCT CAST(strftime('%Y', datetime(fechaVenta / 1000, 'unixepoch')) AS INTEGER) AS anio
    FROM venta
    ORDER BY anio DESC
    """
    )
    suspend fun getAniosConVentas(): List<Int>
}
