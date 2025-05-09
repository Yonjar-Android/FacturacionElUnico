package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.client.ClientDomainModel
import com.example.facturacionelunico.domain.models.client.DetailedClientDomainModel
import com.example.facturacionelunico.domain.models.client.DetailedClientLocalModel
import com.example.facturacionelunico.domain.models.ResultPattern
import kotlinx.coroutines.flow.Flow

interface ClientRepository {

    fun getClients(): Flow<ResultPattern<List<DetailedClientLocalModel>>>

    suspend fun getClientBySearch(query: String): Flow<ResultPattern<List<DetailedClientLocalModel>>>

    suspend fun getClientById(id: Long): Flow<DetailedClientDomainModel>

    suspend fun createClient(client: ClientDomainModel): String

    suspend fun updateClient(client: ClientDomainModel): String

}