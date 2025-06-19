package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.DetalleAbonoVentaEntity

@Dao
interface DetalleAbonoVentaDao {
    @Insert
    suspend fun insert(detalle: DetalleAbonoVentaEntity)

    @Query("SELECT * FROM detalle_abono_venta")
    suspend fun getAll(): List<DetalleAbonoVentaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(detalles: List<DetalleAbonoVentaEntity>)

    @Query("DELETE FROM detalle_abono_venta")
    suspend fun deleteAll()

    @Query("SELECT * FROM detalle_abono_venta WHERE idAbonoVenta = :abonoId")
    suspend fun getAllByAbonoId(abonoId: Long): List<DetalleAbonoVentaEntity>
}
