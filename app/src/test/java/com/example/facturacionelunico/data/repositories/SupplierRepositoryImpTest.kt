package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.ProveedorDao
import com.example.facturacionelunico.data.database.entities.ProveedorEntity
import com.example.facturacionelunico.data.repositories.motherObjects.MotherObjectClientSuppliers
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.SupplierDomainModel
import com.example.facturacionelunico.domain.repositories.SupplierRepository
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

class SupplierRepositoryImpTest {
    @MockK
    lateinit var supplierDao: ProveedorDao

    lateinit var repository: SupplierRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = SupplierRepositoryImp(supplierDao)
    }

    @Test
    fun `getSuppliers should return Success with mapped data`() = runTest {

        // Given
        val response = MotherObjectClientSuppliers.listEntitySupplier
        every { supplierDao.getAll() } returns flow { emit(response) }

        // When

        val resultFlow: Flow<ResultPattern<List<SupplierDomainModel>>> = repository.getSuppliers()
        val results = mutableListOf<ResultPattern<List<SupplierDomainModel>>>()

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
            assertEquals(expectedClients.first().nombreEmpresa, data[0].company)
        }
    }

    @Test
    fun `getSuppliers should return Success with no data`() = runTest {

        // Given
        val response = flow { emit(emptyList<ProveedorEntity>()) }
        every { supplierDao.getAll() } returns response

        // When

        val resultFlow: Flow<ResultPattern<List<SupplierDomainModel>>> = repository.getSuppliers()
        val results = mutableListOf<ResultPattern<List<SupplierDomainModel>>>()

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
    fun `getSuppliers should return Error when an exception occurs`() = runTest {

        // Given
        val exception = RuntimeException("Simulated error")
        every { supplierDao.getAll() } returns flow { throw exception }

        // When

        val resultFlow: Flow<ResultPattern<List<SupplierDomainModel>>> = repository.getSuppliers()
        val results = mutableListOf<ResultPattern<List<SupplierDomainModel>>>()

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
    fun `getSupplierById should return Success with mapped data`() = runTest {

        // Given
        val client = MotherObjectClientSuppliers.oneEntitySupplier
        val id = 1L

        coEvery { supplierDao.getSupplierById(id) } returns flow { emit(client) }

        // When

        val result: Flow<SupplierDomainModel?> = repository.getSupplierById(id)

        val finalResult = result.first()
        // Then
        assertEquals(finalResult?.id, client.id)
        assertEquals(finalResult?.id, id)
        assertNotNull(finalResult)
        coVerify(exactly = 1) { supplierDao.getSupplierById(id) }
    }

    @Test
    fun `getSuppliersBySearch should return Success with mapped data`() = runTest {

        // Given
        val clients = flow { emit(MotherObjectClientSuppliers.listDomainSupplier) }
        val query = "Ken"

        coEvery { supplierDao.getSuppliersBySearch(query) } returns clients

        // When

        val resultFlow: Flow<ResultPattern<List<SupplierDomainModel?>>> = repository.getSuppliersBySearch(query)
        val finalResult = resultFlow.first()

        val result = finalResult as ResultPattern.Success
        val data = result.data.first() // Lista de productos esperados

        val response = clients.first().first() // Primer elemento de la lista recibido desde room

        // Then
        assertEquals(data?.id, response.id)
        assertEquals(data?.company, response.company)
        assertNotNull(data)
        assertTrue(data?.company?.contains(query) == true)
        coVerify(exactly = 1) { supplierDao.getSuppliersBySearch(query) }
    }

    @Test
    fun `getSuppliersBySearch should return Success with no data`() = runTest {

        // Given
        val suppliers = flow { emit(emptyList<SupplierDomainModel>()) }
        val query = "Ken"

        coEvery { supplierDao.getSuppliersBySearch(query) } returns suppliers

        // When

        val resultFlow: Flow<ResultPattern<List<SupplierDomainModel?>>> = repository.getSuppliersBySearch(query)
        val finalResult = resultFlow.first()

        val result = finalResult as ResultPattern.Success
        val data = result.data // Lista de productos esperados, en este caso 0

        val response = suppliers.first() // Lista recibida desde room

        // Then
        assertEquals(data.size, 0)
        assertEquals(response.size, 0)
        coVerify(exactly = 1) { supplierDao.getSuppliersBySearch(query) }
    }

    @Test
    fun `getSuppliersBySearch should return Error when an exception occurs`() = runTest {

        // Given
        val query = "Ken"

        coEvery { supplierDao.getSuppliersBySearch(query) } returns flow { throw RuntimeException("Simulated Error") }

        // When

        val resultFlow: Flow<ResultPattern<List<SupplierDomainModel?>>> = repository.getSuppliersBySearch(query)
        val finalResult = resultFlow.first()

        val result = finalResult as ResultPattern.Error

        // Then
        assertEquals(result.message, "Error: Simulated Error")
        coVerify(exactly = 1) { supplierDao.getSuppliersBySearch(query) }
    }

    @Test
    fun `createSupplier should return success message when supplier is created successfully`() = runTest {

        // Given
        val entity = MotherObjectClientSuppliers.oneEntitySupplier
        val domainModel = MotherObjectClientSuppliers.oneDomainSupplier

        coEvery { supplierDao.insert(entity.copy(id = 0)) } returns Unit
        coEvery { supplierDao.getSupplierByCompany(domainModel.company, domainModel.id) } returns null

        // When

        val result: String = repository.createSupplier(domainModel)

        // Then
        assertEquals("Se ha creado un nuevo proveedor", result)
        coVerify(exactly = 1) { supplierDao.insert(entity.copy(id = 0)) }
        coVerify(exactly = 1) { supplierDao.getSupplierByCompany(domainModel.company, domainModel.id) }
    }

    @Test
    fun `createClient should return error message when client already exists`() = runTest {

        // Given
        val entity = MotherObjectClientSuppliers.oneEntitySupplier
        val domainModel = MotherObjectClientSuppliers.oneDomainSupplier

        coEvery { supplierDao.getSupplierByCompany(domainModel.company, domainModel.id) } returns entity

        // When

        val result: String = repository.createSupplier(domainModel)

        // Then
        assertEquals("Error: Ya existe un proveedor con ese nombre de empresa", result)
        coVerify(exactly = 1) { supplierDao.getSupplierByCompany(domainModel.company, domainModel.id) }
    }

    @Test
    fun `createClient should return error message when an exception occurs`() = runTest {

        // Given
        val entity = MotherObjectClientSuppliers.oneEntitySupplier
        val domainModel = MotherObjectClientSuppliers.oneDomainSupplier

        coEvery { supplierDao.insert(entity.copy(id = 0)) } throws RuntimeException("Simulated error")
        coEvery { supplierDao.getSupplierByCompany(domainModel.company, domainModel.id) } returns null

        // When

        val result: String = repository.createSupplier(domainModel)

        // Then
        assertEquals("Error: Simulated error", result)
        coVerify(exactly = 1) { supplierDao.insert(entity.copy(id = 0)) }
        coVerify(exactly = 1) { supplierDao.getSupplierByCompany(domainModel.company, domainModel.id) }
    }

    @Test
    fun `updateSupplier should return success message when supplier is updated successfully`() = runTest {

        // Given
        val entity = MotherObjectClientSuppliers.oneEntitySupplier
        val domainModel = MotherObjectClientSuppliers.oneDomainSupplier

        coEvery { supplierDao.update(entity) } returns Unit
        coEvery { supplierDao.getSupplierByCompany(domainModel.company, domainModel.id) } returns null
        // When

        val result: String = repository.updateSupplier(domainModel)

        // Then
        assertEquals("Se ha actualizado el proveedor", result)
        coVerify(exactly = 1) { supplierDao.update(entity) }
        coVerify(exactly = 1) { supplierDao.getSupplierByCompany(domainModel.company, domainModel.id) }
    }

    @Test
    fun `updateSupplier should return error message when company's name already exists in other supplier`() = runTest {

        // Given
        val entity = MotherObjectClientSuppliers.oneEntitySupplier
        val domainModel = MotherObjectClientSuppliers.oneDomainSupplier

        coEvery { supplierDao.getSupplierByCompany(domainModel.company, domainModel.id) } returns entity
        // When

        val result: String = repository.updateSupplier(domainModel)

        // Then
        assertEquals("Error: Ya existe un proveedor con ese nombre de empresa", result)
        coVerify(exactly = 1) { supplierDao.getSupplierByCompany(domainModel.company, domainModel.id) }
    }

    @Test
    fun `updateSupplier should return error message when an exception occurs`() = runTest {

        // Given
        val entity = MotherObjectClientSuppliers.oneEntitySupplier
        val domainModel = MotherObjectClientSuppliers.oneDomainSupplier

        coEvery { supplierDao.update(entity) } throws RuntimeException("Simulated error")
        coEvery { supplierDao.getSupplierByCompany(domainModel.company, domainModel.id) } returns null

        // When

        val result: String = repository.updateSupplier(domainModel)

        // Then
        assertEquals("Error: Simulated error", result)
        coVerify(exactly = 1) { supplierDao.update(entity) }
        coVerify(exactly = 1) { supplierDao.getSupplierByCompany(domainModel.company, domainModel.id) }
    }

}