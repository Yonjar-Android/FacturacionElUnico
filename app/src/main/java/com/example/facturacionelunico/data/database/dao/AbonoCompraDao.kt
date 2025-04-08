package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.facturacionelunico.data.database.entities.AbonoCompraEntity

@Dao
interface AbonoCompraDao {
    @Insert
    suspend fun insert(abono: AbonoCompraEntity)

    @Query("SELECT * FROM abono_compra")
    suspend fun getAll(): List<AbonoCompraEntity>
}
