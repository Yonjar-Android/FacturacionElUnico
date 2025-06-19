package com.example.facturacionelunico.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.CompraEntity
import com.example.facturacionelunico.domain.models.purchase.PurchaseDetailLocalModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {
    @Insert
    suspend fun insert(compra: CompraEntity): Long

    @Update
    suspend fun update(compra: CompraEntity)

    @Query("SELECT * FROM compra ORDER BY estado DESC")
    fun getAll(): PagingSource<Int, CompraEntity>

    @Query("SELECT * FROM compra WHERE id = :id")
    suspend fun getPurchaseById(id: Long): CompraEntity

    @Query("""SELECT * FROM compra
        WHERE estado = 'PENDIENTE'
        ORDER BY estado DESC
    """)
    fun getPurchasesWithDebt(): PagingSource<Int, CompraEntity>

    @Query("""
        SELECT c.id as id,
        p.nombreEmpresa as company,
        c.total as total,
        IFNULL(SUM(ac.totalPendiente), 0.0) as totalPendiente
        from compra c
        LEFT JOIN proveedor p on p.id = c.idProveedor
        LEFT JOIN abono_compra ac on ac.idCompra = c.id
        WHERE c.id = :id
        GROUP BY c.id, c.total
        """)
    fun getPurchaseDetailById(id: Long): Flow<PurchaseDetailLocalModel>

    @Query("""
        SELECT * FROM compra WHERE idProveedor = :id
        ORDER BY 
            CASE WHEN estado = 'PENDIENTE' THEN 0 ELSE 1 END,
             fechaCompra DESC
    """)
    fun getPurchasesBySupplierId(id: Long): PagingSource<Int, CompraEntity>
}
