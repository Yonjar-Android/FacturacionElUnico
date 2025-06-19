package com.example.facturacionelunico.domain.repositories

import androidx.paging.PagingData
import com.example.facturacionelunico.domain.models.client.ClientDomainModel
import com.example.facturacionelunico.domain.models.client.DetailedClientDomainModel
import com.example.facturacionelunico.domain.models.client.DetailedClientLocalModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import kotlinx.coroutines.flow.Flow

interface ClientRepository {

    fun getClients(): Flow<ResultPattern<PagingData<DetailedClientLocalModel>>>

    suspend fun getClientBySearch(query: String): Flow<ResultPattern<PagingData<DetailedClientLocalModel>>>

    suspend fun getClientById(id: Long): Flow<DetailedClientDomainModel>

    fun getInvoicesByClientId(clientId:Long): Flow<PagingData<InvoiceDomainModel>>

    suspend fun createClient(client: ClientDomainModel): String

    suspend fun updateClient(client: ClientDomainModel): String

}