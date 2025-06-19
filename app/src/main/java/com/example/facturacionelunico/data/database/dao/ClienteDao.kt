package com.example.facturacionelunico.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.ClienteEntity
import com.example.facturacionelunico.domain.models.client.DetailedClientLocalModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Insert
    suspend fun insert(cliente: ClienteEntity)

    @Update
    suspend fun update(cliente: ClienteEntity)

    @Query("SELECT * FROM cliente")
    fun getAll(): PagingSource<Int, ClienteEntity>

    @Query(
        """
    SELECT 
        c.id AS id,
        c.nombre AS name,
        c.apellido AS lastName,
        c.telefono AS phone,
        c.identificadorCliente AS numberIdentifier,
        IFNULL(SUM(a.totalPendiente), 0.0) AS deptTotal
    FROM cliente c
    LEFT JOIN venta v ON c.id = v.idCliente
    LEFT JOIN abono_venta a ON v.id = a.idVenta
    GROUP BY c.id
    ORDER BY deptTotal DESC
"""
    )
    fun getClientsWithDebt(): PagingSource<Int, DetailedClientLocalModel>

    @Query("""
    SELECT 
        c.id as id,
        c.nombre as name,
        c.apellido as lastName,
        c.telefono as phone,
        c.identificadorCliente as numberIdentifier,
        IFNULL(SUM(a.totalPendiente), 0.0) as deptTotal
    FROM cliente c
    LEFT JOIN venta v ON c.id = v.idCliente
    LEFT JOIN abono_venta a ON v.id = a.idVenta
    WHERE c.id = :id 
    GROUP BY c.id
    ORDER BY deptTotal DESC
""")
    fun getClientById(id: Long): Flow<DetailedClientLocalModel>


    @Query("SELECT * FROM cliente WHERE identificadorCliente = :identificador AND id != :currentId")
    suspend fun getClienteByIdentificadorExcludingId(
        identificador: Int,
        currentId: Long
    ): ClienteEntity?

    @Query(
        """
    SELECT 
        c.id as id,
        c.nombre as name,
        c.apellido as lastName,
        c.telefono as phone,
        c.identificadorCliente as numberIdentifier,
        IFNULL(SUM(a.totalPendiente), 0.0) as deptTotal
    FROM cliente c
    LEFT JOIN venta v ON c.id = v.idCliente
    LEFT JOIN abono_venta a ON v.id = a.idVenta
    WHERE (c.nombre || ' ' || c.apellido) LIKE '%' || :query || '%'
    GROUP BY c.id
    ORDER BY deptTotal DESC
"""
    )
    fun getClientByName(query: String): PagingSource<Int, DetailedClientLocalModel>
}

