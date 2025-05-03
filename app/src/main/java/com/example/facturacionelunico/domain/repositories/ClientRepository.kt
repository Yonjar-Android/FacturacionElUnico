package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.ClientDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import kotlinx.coroutines.flow.Flow

interface ClientRepository {

    fun getClients(): Flow<ResultPattern<List<ClientDomainModel>>>

    suspend fun getClientBySearch(query: String): Flow<ResultPattern<List<ClientDomainModel>>>

    //fun getClientById(id: Long): Flow<ResultPattern<ClientDomainModel>>

    suspend fun createClient(client: ClientDomainModel): String

    suspend fun updateClient(client: ClientDomainModel): String

}