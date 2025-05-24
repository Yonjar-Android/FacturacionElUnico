package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.ClienteDao
import com.example.facturacionelunico.data.database.dao.VentaDao
import com.example.facturacionelunico.data.database.entities.ClienteEntity
import com.example.facturacionelunico.data.repositories.motherObjects.MotherObjectClientSuppliers
import com.example.facturacionelunico.domain.models.client.ClientDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ClientRepositoryImpTest {
    @MockK
    lateinit var clienteDao: ClienteDao

    @MockK
    lateinit var invoiceDao: VentaDao

    lateinit var repository: ClientRepositoryImp

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = ClientRepositoryImp(clienteDao, invoiceDao)
    }

    @Test
    fun `getClients should return Success with mapped data`() = runTest {

        // Given
        val response = MotherObjectClientSuppliers.listEntityClient
        every { clienteDao.getAll() } returns flow { emit(response) }

        // When

        val resultFlow: Flow<ResultPattern<List<ClientDomainModel>>> = repository.getClients()
        val results = mutableListOf<ResultPattern<List<ClientDomainModel>>>()

        resultFlow.catch { e ->
            results.add(ResultPattern.Error(e, e.message))
        }.collect {
            results.add(it)
        }

        // Then
        assertEquals(1, results.size)
        val result = results.first()

        assertTrue(result is ResultPattern.Success)

        val expectedClients = response // Lista de clientes esperados

        if (result is ResultPattern.Success) {
            val data = result.data
            assertEquals(expectedClients.size, data.size)
            assertEquals(expectedClients.first().id, data[0].id)
            assertEquals(expectedClients.first().nombre, data[0].name)
        }
    }

    @Test
    fun `getClients should return Success with no data`() = runTest {

        // Given
        val response = flow { emit(emptyList<ClienteEntity>()) }
        every { clienteDao.getAll() } returns response

        // When

        val resultFlow: Flow<ResultPattern<List<ClientDomainModel>>> = repository.getClients()
        val results = mutableListOf<ResultPattern<List<ClientDomainModel>>>()

        resultFlow.catch { e ->
            results.add(ResultPattern.Error(e, e.message))
        }.collect {
            results.add(it)
        }

        // Then
        assertEquals(1, results.size)
        val result = results.first()

        assertTrue(result is ResultPattern.Success)

        val expectedProducts = response.first() // Lista de Productos esperados

        if (result is ResultPattern.Success) {
            val data = result.data
            assertEquals(expectedProducts.size, data.size)
        }
    }

    @Test
    fun `getClients should return Error when an exception occurs`() = runTest {

        // Given
        val exception = RuntimeException("Simulated error")
        every { clienteDao.getAll() } returns flow { throw exception }

        // When

        val resultFlow: Flow<ResultPattern<List<ClientDomainModel>>> = repository.getClients()
        val results = mutableListOf<ResultPattern<List<ClientDomainModel>>>()

        resultFlow.catch { e ->
            results.add(ResultPattern.Error(e, e.message))
        }.collect {
            results.add(it)
        }

        // Then
        assertEquals(1, results.size)
        val result = results.first()

        assertTrue(result is ResultPattern.Error)

        if (result is ResultPattern.Error) {
            assertEquals(exception, result.exception)
        }
    }

    @Test
    fun `getClientById should return Success with mapped data`() = runTest {

        // Given
        val client = MotherObjectClientSuppliers.oneEntityClient
        val id = 1L

        coEvery { clienteDao.getClientById(id) } returns flow { emit(client) }

        // When

        val result: Flow<ClientDomainModel?> = repository.getClientById(id)

        val finalResult = result.first()
        // Then
        assertEquals(finalResult?.id, client.id)
        assertEquals(finalResult?.id, id)
        assertNotNull(finalResult)
        coVerify(exactly = 1) { clienteDao.getClientById(id) }
    }

    @Test
    fun `getClientBySearch should return Success with mapped data`() = runTest {

        // Given
        val clients = flow { emit(listOf(MotherObjectClientSuppliers.oneDomainClient)) }
        val query = "José"

        coEvery { clienteDao.getClientByName(query) } returns clients

        // When

        val resultFlow: Flow<ResultPattern<List<ClientDomainModel?>>> = repository.getClientBySearch(query)
        val finalResult = resultFlow.first()

        val result = finalResult as ResultPattern.Success
        val data = result.data.first() // Lista de productos esperados

        val response = clients.first().first() // Primer elemento de la lista recibido desde room

        // Then
        assertEquals(data?.id, response.id)
        assertEquals(data?.name, response.name)
        assertNotNull(data)
        assertTrue(data?.name?.contains(query) == true)
        coVerify(exactly = 1) { clienteDao.getClientByName(query) }
    }

    @Test
    fun `getClientBySearch should return Success with no data`() = runTest {

        // Given
        val clients = flow { emit(emptyList<ClientDomainModel>()) }
        val query = "José"

        coEvery { clienteDao.getClientByName(query) } returns clients

        // When

        val resultFlow: Flow<ResultPattern<List<ClientDomainModel?>>> = repository.getClientBySearch(query)
        val finalResult = resultFlow.first()

        val result = finalResult as ResultPattern.Success
        val data = result.data // Lista de productos esperados, en este caso 0

        val response = clients.first() // Lista recibida desde room

        // Then
        assertEquals(data.size, 0)
        assertEquals(response.size, 0)
        coVerify(exactly = 1) { clienteDao.getClientByName(query) }
    }

    @Test
    fun `getClientBySearch should return Error when an exception occurs`() = runTest {

        // Given
        val query = "Jos"

        coEvery { clienteDao.getClientByName(query) } returns flow { throw RuntimeException("Simulated Error") }

        // When

        val resultFlow: Flow<ResultPattern<List<ClientDomainModel?>>> = repository.getClientBySearch(query)
        val finalResult = resultFlow.first()

        val result = finalResult as ResultPattern.Error

        // Then
        assertEquals(result.message, "Error: Simulated Error")
        coVerify(exactly = 1) { clienteDao.getClientByName(query) }
    }

    @Test
    fun `createClient should return success message when client is created successfully`() = runTest {

        // Given
        val entity = MotherObjectClientSuppliers.oneEntityClient
        val domainModel = MotherObjectClientSuppliers.oneDomainClient

        coEvery { clienteDao.insert(entity.copy(id = 0)) } returns Unit
        coEvery { clienteDao.getClienteByIdentificadorExcludingId(domainModel.numberIdentifier, domainModel.id) } returns null

        // When

        val result: String = repository.createClient(domainModel)

        // Then
        assertEquals("Se ha creado un nuevo cliente", result)
        coVerify(exactly = 1) { clienteDao.insert(entity.copy(id = 0)) }
        coVerify(exactly = 1) { clienteDao.getClienteByIdentificadorExcludingId(domainModel.numberIdentifier, domainModel.id) }
    }

    @Test
    fun `createClient should return error message when client already exists`() = runTest {

        // Given
        val entity = MotherObjectClientSuppliers.oneEntityClient
        val domainModel = MotherObjectClientSuppliers.oneDomainClient

        coEvery { clienteDao.getClienteByIdentificadorExcludingId(domainModel.numberIdentifier, domainModel.id) } returns entity

        // When

        val result: String = repository.createClient(domainModel)

        // Then
        assertEquals("Error: Ya existe un cliente con ese código", result)
        coVerify(exactly = 1) { clienteDao.getClienteByIdentificadorExcludingId(domainModel.numberIdentifier, domainModel.id) }
    }

    @Test
    fun `createClient should return error message when an exception occurs`() = runTest {

        // Given
        val entity = MotherObjectClientSuppliers.oneEntityClient
        val domainModel = MotherObjectClientSuppliers.oneDomainClient

        coEvery { clienteDao.insert(entity.copy(id = 0)) } throws RuntimeException("Simulated error")
        coEvery { clienteDao.getClienteByIdentificadorExcludingId(domainModel.numberIdentifier, domainModel.id) } returns null

        // When

        val result: String = repository.createClient(domainModel)

        // Then
        assertEquals("Error: Simulated error", result)
        coVerify(exactly = 1) { clienteDao.insert(entity.copy(id = 0)) }
        coVerify(exactly = 1) { clienteDao.getClienteByIdentificadorExcludingId(domainModel.numberIdentifier, domainModel.id) }
    }

    @Test
    fun `updateClient should return success message when client is updated successfully`() = runTest {

        // Given
        val entity = MotherObjectClientSuppliers.oneEntityClient
        val domainModel = MotherObjectClientSuppliers.oneDomainClient

        coEvery { clienteDao.update(entity) } returns Unit
        coEvery { clienteDao.getClienteByIdentificadorExcludingId(domainModel.numberIdentifier, domainModel.id) } returns null
        // When

        val result: String = repository.updateClient(domainModel)

        // Then
        assertEquals("Se ha actualizado el cliente", result)
        coVerify(exactly = 1) { clienteDao.update(entity) }
        coVerify(exactly = 1) { clienteDao.getClienteByIdentificadorExcludingId(domainModel.numberIdentifier, domainModel.id) }
    }

    @Test
    fun `updateClient should return error message when client's numberIdentifier already exists in other client`() = runTest {

        // Given
        val entity = MotherObjectClientSuppliers.oneEntityClient
        val domainModel = MotherObjectClientSuppliers.oneDomainClient

        coEvery { clienteDao.getClienteByIdentificadorExcludingId(domainModel.numberIdentifier, domainModel.id) } returns entity
        // When

        val result: String = repository.updateClient(domainModel)

        // Then
        assertEquals("Error: Ya existe un cliente con ese código", result)
        coVerify(exactly = 1) { clienteDao.getClienteByIdentificadorExcludingId(domainModel.numberIdentifier, domainModel.id) }
    }

    @Test
    fun `updateClient should return error message when an exception occurs`() = runTest {

        // Given
        val entity = MotherObjectClientSuppliers.oneEntityClient
        val domainModel = MotherObjectClientSuppliers.oneDomainClient

        coEvery { clienteDao.update(entity) } throws RuntimeException("Simulated error")
        coEvery { clienteDao.getClienteByIdentificadorExcludingId(domainModel.numberIdentifier, domainModel.id) } returns null

        // When

        val result: String = repository.updateClient(domainModel)

        // Then
        assertEquals("Error: Simulated error", result)
        coVerify(exactly = 1) { clienteDao.update(entity) }
        coVerify(exactly = 1) { clienteDao.getClienteByIdentificadorExcludingId(domainModel.numberIdentifier, domainModel.id) }
    }
}