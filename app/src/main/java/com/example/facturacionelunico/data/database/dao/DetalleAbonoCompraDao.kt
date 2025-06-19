package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.DetalleAbonoCompraEntity

@Dao
interface DetalleAbonoCompraDao {
    @Insert
    suspend fun insert(detalle: DetalleAbonoCompraEntity)

    @Query("SELECT * FROM detalle_abono_compra")
    suspend fun getAll(): List<DetalleAbonoCompraEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(detalles: List<DetalleAbonoCompraEntity>)

    @Query("DELETE FROM detalle_abono_compra")
    suspend fun deleteAll()

    @Query("""
        SELECT * FROM detalle_abono_compra
        WHERE idAbonoCompra = :id
    """)
    suspend fun getAllByAbonoId(id: Long): List<DetalleAbonoCompraEntity>
}
