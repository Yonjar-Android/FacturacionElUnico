package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.ClienteDao
import com.example.facturacionelunico.data.database.entities.ClienteEntity
import com.example.facturacionelunico.domain.models.ClientDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClientRepositoryImp @Inject constructor(
    private val clientDao: ClienteDao
): ClientRepository {
    // Función para obtener a todos los clientes mediante un flow
    override fun getClients(): Flow<ResultPattern<List<ClientDomainModel>>> {
        return clientDao.getAll()
            .map {
                it.map {
                    ClientDomainModel(
                        id = it.id,
                        name = it.nombre,
                        lastName = it.apellido,
                        phone = it.telefono,
                        numberIdentifier = it.identificadorCliente)
                }
            }.map<List<ClientDomainModel>, ResultPattern<List<ClientDomainModel>>> { suppliers ->
                ResultPattern.Success(suppliers)
            }.catch { e ->
                emit(ResultPattern.Error(exception = e, message = e.message))
            }
    }

    override suspend fun getClientBySearch(query: String): Flow<ResultPattern<List<ClientDomainModel>>> {
        return clientDao.getClientByName(query)
            .map<List<ClientDomainModel>, ResultPattern<List<ClientDomainModel>>> { products ->
                ResultPattern.Success(products)
            }
            .catch { e ->
                emit(ResultPattern.Error(exception = e, message = "Error: ${e.message}"))
            }
    }

    override fun getClientById(id: Long): Flow<ClientDomainModel> {
        return clientDao.getClientById(id).map {
            ClientDomainModel(
                id = it.id,
                name = it.nombre,
                lastName = it.apellido,
                phone = it.telefono,
                numberIdentifier = it.identificadorCliente)
        }
    }

    // Función para crear un cliente
    override suspend fun createClient(client: ClientDomainModel): String {
        return runCatching {

            // Función para verificar si ya existe un cliente con el mismo código proporcionado
            val existingClient = clientDao.getClienteByIdentificadorExcludingId(client.numberIdentifier,client.id)

            if (existingClient != null){
                return "Error: Ya existe un cliente con ese código"
            } else{
                val newClient = ClienteEntity(
                    nombre = client.name,
                    apellido = client.lastName,
                    telefono = client.phone ?: "",
                    identificadorCliente = client.numberIdentifier
                )
                clientDao.insert(newClient)
                "Se ha creado un nuevo cliente"
            }
        }.getOrElse {
           "Error: ${it.message}"
        }
    }

    override suspend fun updateClient(client: ClientDomainModel): String {
        return runCatching {

            // Función para verificar si ya existe un cliente con el mismo código proporcionado
            val existingClient = clientDao.getClienteByIdentificadorExcludingId(client.numberIdentifier, client.id)

            if (existingClient != null){
                return "Error: Ya existe un cliente con ese código"
            } else{
                val newClient = ClienteEntity(
                    id = client.id,
                    nombre = client.name,
                    apellido = client.lastName,
                    telefono = client.phone ?: "",
                    identificadorCliente = client.numberIdentifier
                )
                clientDao.update(newClient)
                "Se ha actualizado el cliente"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }
}