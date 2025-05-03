package com.example.facturacionelunico.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.ClienteEntity
import com.example.facturacionelunico.domain.models.ClientDomainModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Insert
    suspend fun insert(cliente: ClienteEntity)

    @Update
    suspend fun update(cliente: ClienteEntity)

    @Query("SELECT * FROM cliente")
    fun getAll(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM cliente WHERE id = :id")
    fun getClientById(id: Long): Flow<ClienteEntity>

    @Query("SELECT * FROM cliente WHERE identificadorCliente = :identificador AND id != :currentId")
    suspend fun getClienteByIdentificadorExcludingId(identificador: Int, currentId: Long): ClienteEntity?

    @Query("""
        SELECT cliente.id as id,
        cliente.nombre as name,
        cliente.apellido as lastName,
        cliente.telefono as phone,
        cliente.identificadorCliente as numberIdentifier
        FROM cliente
        WHERE (nombre || ' ' || apellido) LIKE '%' || :query || '%'
    """)
    fun getClientByName(query:String): Flow<List<ClientDomainModel>>
}

