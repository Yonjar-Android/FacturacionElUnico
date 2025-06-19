package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.AbonoVentaEntity

@Dao
interface AbonoVentaDao {
    @Insert
    suspend fun insert(abono: AbonoVentaEntity):Long

    @Update
    suspend fun update(abono: AbonoVentaEntity)

    @Query("SELECT * FROM abono_venta")
    suspend fun getAll(): List<AbonoVentaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(abonos: List<AbonoVentaEntity>)

    @Query("DELETE FROM abono_venta")
    suspend fun deleteAll()

    @Query("SELECT * FROM abono_venta WHERE idVenta = :id")
    suspend fun getAbonoByInvoiceId(id: Long): AbonoVentaEntity
}
