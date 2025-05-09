package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.VentaEntity

@Dao
interface VentaDao {
    @Insert
    suspend fun insert(venta: VentaEntity): Long

    @Query("SELECT * FROM venta")
    suspend fun getAll(): List<VentaEntity>

    @Query("""
    SELECT * FROM venta
    WHERE idCliente = :id
    ORDER BY 
        CASE WHEN estado = 'PENDIENTE' THEN 0 ELSE 1 END,
        fechaVenta DESC
""")
    suspend fun getInvoicesByClientId(id: Long): List<VentaEntity>
}
