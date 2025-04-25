package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.ProductoDao
import com.example.facturacionelunico.data.mappers.ProductMapper
import com.example.facturacionelunico.domain.models.DetailedProductModel
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

class ProductRepositoryImpTest {

    @MockK
    lateinit var productDao: ProductoDao

    lateinit var repository: ProductRepositoryImp

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = ProductRepositoryImp(productDao)
    }

    @Test
    fun `getProducts should return Success with mapped data`() = runTest {

        // Given
        val response = MotherObjectRepositories.flowProductDetailed
        every { productDao.getAllDetailed() } returns response

        // When

        val resultFlow: Flow<ResultPattern<List<DetailedProductModel>>> = repository.getProducts()
        val results = mutableListOf<ResultPattern<List<DetailedProductModel>>>()

        resultFlow.catch { e ->
            results.add(ResultPattern.Error(e, e.message))
        }.collect {
            results.add(it)
        }

        // Then
        assertEquals(1, results.size)
        val result = results.first()

        assertTrue(result is ResultPattern.Success)

        val expectedProducts = response.first() // Lista de productos esperados

        if (result is ResultPattern.Success) {
            val data = result.data
            assertEquals(expectedProducts.size, data.size)
            assertEquals(expectedProducts.first().id, data[0].id)
            assertEquals(expectedProducts.first().name, data[0].name)
        }
    }

    @Test
    fun `getProducts should return Success with no data`() = runTest {

        // Given
        val response = flow { emit(emptyList<DetailedProductModel>()) }
        every { productDao.getAllDetailed() } returns response

        // When

        val resultFlow: Flow<ResultPattern<List<DetailedProductModel>>> = repository.getProducts()
        val results = mutableListOf<ResultPattern<List<DetailedProductModel>>>()

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
    fun `getProducts should return Error when an exception occurs`() = runTest {

        // Given
        val exception = RuntimeException("Simulated error")
        every { productDao.getAllDetailed() } returns flow { throw exception }

        // When

        val resultFlow: Flow<ResultPattern<List<DetailedProductModel>>> = repository.getProducts()
        val results = mutableListOf<ResultPattern<List<DetailedProductModel>>>()

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
    fun `getProductById should return Success with mapped data`() = runTest {

        // Given
        val product = MotherObjectRepositories.productDetailed
        val id = 201L

        coEvery { productDao.getDetailedById(id) } returns product

        // When

        val result: ResultPattern<DetailedProductModel?> = repository.getProductById(id)

        val finalResult = result as ResultPattern.Success
        // Then
        assertEquals(finalResult.data?.id, product.id)
        assertEquals(finalResult.data?.id, id)
        assertNotNull(finalResult.data)
        coVerify(exactly = 1) { productDao.getDetailedById(id) }
    }

    @Test
    fun `getProductById should return Error when product is not found`() = runTest {

        // Given
        val product = null
        val id = 201L

        coEvery { productDao.getDetailedById(id) } returns product

        // When

        val result: ResultPattern<DetailedProductModel?> = repository.getProductById(id)

        val finalResult = result as ResultPattern.Error
        // Then
        assertEquals(finalResult.message, "Error: Producto no encontrado")
        assertEquals(product, null)
        coVerify(exactly = 1) { productDao.getDetailedById(id) }
    }

    @Test
    fun `getProductById should return Error when an exception occurs`() = runTest {

        // Given
        val id = 201L

        coEvery { productDao.getDetailedById(id) } throws RuntimeException("Simulated Error")

        // When

        val result: ResultPattern<DetailedProductModel?> = repository.getProductById(id)

        val finalResult = result as ResultPattern.Error
        // Then
        assertEquals(finalResult.message, "Error: Simulated Error")
        coVerify(exactly = 1) { productDao.getDetailedById(id) }
    }

    @Test
    fun `getProductBySearch should return Success with mapped data`() = runTest {

        // Given
        val product = MotherObjectRepositories.flowProductDetailed
        val query = "Kend"

        coEvery { productDao.getProductsBySearch(query) } returns product

        // When

        val resultFlow: Flow<ResultPattern<List<DetailedProductModel?>>> = repository.getProductBySearch(query)
        val finalResult = resultFlow.first()

        val result = finalResult as ResultPattern.Success
        val data = result.data.first() // Lista de productos esperados

        val response = product.first().first() // Primer elemento de la lista recibido desde room

        // Then
        assertEquals(data?.id, response.id)
        assertEquals(data?.name, response.name)
        assertNotNull(data)
        assertTrue(data?.name?.contains(query) == true)
        coVerify(exactly = 1) { productDao.getProductsBySearch(query) }
    }

    @Test
    fun `getProductBySearch should return Success with no data`() = runTest {

        // Given
        val product = flow { emit(emptyList<DetailedProductModel>()) }
        val query = "Kendasssss"

        coEvery { productDao.getProductsBySearch(query) } returns product

        // When

        val resultFlow: Flow<ResultPattern<List<DetailedProductModel?>>> = repository.getProductBySearch(query)
        val finalResult = resultFlow.first()

        val result = finalResult as ResultPattern.Success
        val data = result.data // Lista de productos esperados, en este caso 0

        val response = product.first() // Lista recibida desde room

        // Then
        assertEquals(data.size, 0)
        assertEquals(response.size, 0)
        coVerify(exactly = 1) { productDao.getProductsBySearch(query) }
    }

    @Test
    fun `getProductBySearch should return Error when an exception occurs`() = runTest {

        // Given
        val query = "Kend"

        coEvery { productDao.getProductsBySearch(query) } returns flow { throw RuntimeException("Simulated Error") }

        // When

        val resultFlow: Flow<ResultPattern<List<DetailedProductModel?>>> = repository.getProductBySearch(query)
        val finalResult = resultFlow.first()

        val result = finalResult as ResultPattern.Error

        // Then
        assertEquals(result.message, "Error: Simulated Error")
        coVerify(exactly = 1) { productDao.getProductsBySearch(query) }
    }

    @Test
    fun `createProduct should return success message when product is created successfully`() = runTest {

        // Given
        val entity = MotherObjectRepositories.oneProductEntity
        val domainModel = ProductMapper.toDomain(entity)

        coEvery { productDao.insert(entity) } returns Unit

        // When

        val result: String = repository.createProduct(domainModel)

        // Then
        assertEquals("Se ha agregado un nuevo producto", result)
        coVerify(exactly = 1) { productDao.insert(entity) }
    }

    @Test
    fun `createProduct should return error message when an exception occurs`() = runTest {

        // Given
        val entity = MotherObjectRepositories.oneProductEntity
        val domainModel = ProductMapper.toDomain(entity)

        coEvery { productDao.insert(entity) } throws RuntimeException("Simulated error")

        // When

        val result: String = repository.createProduct(domainModel)

        // Then
        assertEquals("Error: Simulated error", result)
        coVerify(exactly = 1) { productDao.insert(entity) }
    }

    @Test
    fun `updateProduct should return success message when product is updated successfully`() = runTest {

        // Given
        val entity = MotherObjectRepositories.oneProductEntity
        val domainModel = ProductMapper.toDomain(entity)

        coEvery { productDao.update(entity) } returns Unit

        // When

        val result: String = repository.updateProduct(domainModel)

        // Then
        assertEquals("Se ha actualizado el producto", result)
        coVerify(exactly = 1) { productDao.update(entity) }
    }

    @Test
    fun `updateProduct should return error message when an exception occurs`() = runTest {

        // Given
        val entity = MotherObjectRepositories.oneProductEntity
        val domainModel = ProductMapper.toDomain(entity)

        coEvery { productDao.update(entity) } throws RuntimeException("Simulated error")

        // When

        val result: String = repository.updateProduct(domainModel)

        // Then
        assertEquals("Error: Simulated error", result)
        coVerify(exactly = 1) { productDao.update(entity) }
    }

}