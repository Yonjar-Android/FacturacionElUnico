package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.AbonoCompraEntity

@Dao
interface AbonoCompraDao {
    @Insert
    suspend fun insert(abono: AbonoCompraEntity): Long

    @Update
    suspend fun update(abono: AbonoCompraEntity)

    @Query("SELECT * FROM abono_compra")
    suspend fun getAll(): List<AbonoCompraEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(abonos: List<AbonoCompraEntity>)

    @Query("DELETE FROM abono_compra")
    suspend fun deleteAll()

    @Query("SELECT * FROM abono_compra WHERE idCompra = :id")
    suspend fun getAbonoByPurchaseId(id: Long): AbonoCompraEntity
}
