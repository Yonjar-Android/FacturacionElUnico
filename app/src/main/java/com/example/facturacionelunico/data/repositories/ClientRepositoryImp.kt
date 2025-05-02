package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.ClienteDao
import com.example.facturacionelunico.data.database.entities.ClienteEntity
import com.example.facturacionelunico.domain.models.ClientDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClientRepositoryImp @Inject constructor(
    private val clientDao: ClienteDao
): ClientRepository {
    override fun getClients(): Flow<ResultPattern<List<ClientDomainModel>>> {
        return clientDao.getAll()
            .map {
                it.map {
                    ClientDomainModel(
                        id = it.id,
                        name = it.nombre,
                        lastName = it.apellido,
                        phone = it.telefono)
                }
            }.map<List<ClientDomainModel>, ResultPattern<List<ClientDomainModel>>> { suppliers ->
                ResultPattern.Success(suppliers)
            }.catch { e ->
                emit(ResultPattern.Error(exception = e, message = e.message))
            }
    }

    override suspend fun createClient(client: ClientDomainModel): String {
        return runCatching {

            val existingClient = clientDao.getClientById(client.id).firstOrNull()

            if (existingClient != null){
                return "Ya existe un cliente con ese código"
            }else{
                val newClient = ClienteEntity(
                    id = client.id,
                    nombre = client.name,
                    apellido = client.lastName,
                    telefono = client.phone ?: ""
                )
                clientDao.insert(newClient)
                "Se ha creado un nuevo cliente"
            }
        }.getOrElse {
           "Error: ${it.message}"
        }
    }
}