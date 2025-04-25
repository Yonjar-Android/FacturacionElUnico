package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.MarcaDao
import com.example.facturacionelunico.data.database.entities.MarcaEntity
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.BrandRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class BrandRepositoryImpTest {

    @MockK
    lateinit var brandDao: MarcaDao

    lateinit var repository: BrandRepository

    @Before
    fun setUp(){
        MockKAnnotations.init(this)
        repository = BrandRepositoryImp(brandDao)
    }

    @Test
    fun `getBrands should return Success with mapped data`() = runTest {

        // Given
        val response = MotherObjectRepositories.flowBrands
        every { brandDao.getAll() } returns response

        // When

        val resultFlow: Flow<ResultPattern<List<BrandDomainModel>>> = repository.getBrands()
        val results = mutableListOf<ResultPattern<List<BrandDomainModel>>>()

        resultFlow.catch { e ->
            results.add(ResultPattern.Error(e, e.message))
        }.collect {
            results.add(it)
        }

        // Then
        assertEquals(1, results.size)
        val result = results.first()

        assertTrue(result is ResultPattern.Success)

        val expectedBrands = response.first() // Lista de marcas esperadas

        if (result is ResultPattern.Success) {
            val data = result.data
            assertEquals(expectedBrands.size, data.size)
            assertEquals(expectedBrands.first().id, data[0].brandId)
            assertEquals(expectedBrands.first().nombre, data[0].brandName)
        }
    }

    @Test
    fun `getBrands should return Success with no data`() = runTest {

        // Given
        val response = flowOf(emptyList<MarcaEntity>())
        every { brandDao.getAll() } returns response

        // When

        val resultFlow: Flow<ResultPattern<List<BrandDomainModel>>> = repository.getBrands()
        val results = mutableListOf<ResultPattern<List<BrandDomainModel>>>()

        resultFlow.catch { e ->
            results.add(ResultPattern.Error(e, e.message))
        }.collect {
            results.add(it)
        }

        // Then
        assertEquals(1, results.size)
        val result = results.first()

        assertTrue(result is ResultPattern.Success)

        if (result is ResultPattern.Success) {
            val data = result.data
            assertEquals(0, data.size)
        }
    }

    @Test
    fun `getBrands should return Error when an exception occurs`() = runTest {

        // Given
        val exception = RuntimeException("Simulated error")
        every { brandDao.getAll() } returns flow { throw exception }

        // When

        val resultFlow: Flow<ResultPattern<List<BrandDomainModel>>> = repository.getBrands()
        val results = mutableListOf<ResultPattern<List<BrandDomainModel>>>()

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
    fun `getBrandByName should return Success with mapped data`() = runTest {

        // Given
        val response = MotherObjectRepositories.flowBrandName
        val search = "Ni"
        every { brandDao.getBrandByName(search) } returns response

        // When

        val resultFlow: Flow<ResultPattern<List<BrandDomainModel>>> = repository.getBrandByName(search)
        val results = mutableListOf<ResultPattern<List<BrandDomainModel>>>()

        resultFlow.catch { e ->
            results.add(ResultPattern.Error(e, e.message))
        }.collect {
            results.add(it)
        }

        // Then
        assertEquals(1, results.size)
        val result = results.first()

        assertTrue(result is ResultPattern.Success)

        val expectedBrands = response.first() // Lista de marcas esperadas

        if (result is ResultPattern.Success) {
            val data = result.data
            assertEquals(expectedBrands.size, data.size)
            assertEquals(expectedBrands.first().id, data[0].brandId)
            assertEquals(expectedBrands.first().nombre, data[0].brandName)
            assertTrue(data[0].brandName.contains(search))
        }
    }

    @Test
    fun `getBrandByName should return Success with no data`() = runTest {

        // Given
        val response = flowOf(emptyList<MarcaEntity>())
        val search = "Ni"
        every { brandDao.getBrandByName(search) } returns response

        // When

        val resultFlow: Flow<ResultPattern<List<BrandDomainModel>>> = repository.getBrandByName(search)
        val results = mutableListOf<ResultPattern<List<BrandDomainModel>>>()

        resultFlow.catch { e ->
            results.add(ResultPattern.Error(e, e.message))
        }.collect {
            results.add(it)
        }

        // Then
        assertEquals(1, results.size)
        val result = results.first()

        assertTrue(result is ResultPattern.Success)

        val expectedBrands = response.first() // Lista de marcas esperadas

        if (result is ResultPattern.Success) {
            val data = result.data
            assertEquals(expectedBrands.size, data.size)
        }
    }

    @Test
    fun `getBrandByName should return Error when an exception occurs`() = runTest {

        // Given
        val exception = RuntimeException("Simulated error")
        val search = "Ni"

        every { brandDao.getBrandByName(search) } returns flow { throw exception }

        // When

        val resultFlow: Flow<ResultPattern<List<BrandDomainModel>>> = repository.getBrandByName(search)
        val results = mutableListOf<ResultPattern<List<BrandDomainModel>>>()

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
    fun `getBrandById should return Success with mapped data`() = runTest {

        // Given
        val brandFlow = MotherObjectRepositories.flowBrandEntity

        coEvery { brandDao.getBrandById(1L) } returns brandFlow

        // When

        val resultFlow: Flow<BrandDomainModel> = repository.getBrandById(1L)
        val result = resultFlow.first()
        val response = brandFlow.first()

        // Then
        assertEquals(result.brandId, response.id)
        coVerify(exactly = 1) { brandDao.getBrandById(1L) }
    }

    @Test
    fun `getProductsByBrand should return Success with mapped data`() = runTest {

        // Given
        val products = MotherObjectRepositories.products

        coEvery { brandDao.getDetailedByBrandId(10L) } returns products

        // When

        val result: List<DetailedProductModel> = repository.getProductsByBrand(10L)

        // Then
        assertEquals(products, result)
        assertEquals(products[0].brandId, 10L)
        coVerify(exactly = 1) { brandDao.getDetailedByBrandId(10L) }
    }

    @Test
    fun `createBrand should return success message when brand is created successfully`() = runTest {

        // Given
        val brandName = "Kenda"
        val entity = MarcaEntity(nombre = brandName)
        coEvery { brandDao.insert(entity) } returns Unit

        // When

        val result: String = repository.createBrand(brandName)

        // Then
        assertEquals("Marca creada exitosamente", result)
        coVerify(exactly = 1) { brandDao.insert(entity) }
    }

    @Test
    fun `createBrand should return error message when an exception occurs`() = runTest {

        // Given
        val brandName = "Kenda"
        val entity = MarcaEntity(nombre = brandName)
        coEvery { brandDao.insert(entity) } throws RuntimeException("Simulated error")

        // When

        val result: String = repository.createBrand(brandName)

        // Then
        assertEquals("Error: Simulated error", result)
        coVerify(exactly = 1) { brandDao.insert(entity) }
    }

    @Test
    fun `updateBrand should return success message when brand is updated successfully`() = runTest {

        // Given
        val brandModel = BrandDomainModel(brandId = 1L, brandName = "Yebram")
        val entity = MarcaEntity(id = 1L, nombre = "Yebram")

        coEvery { brandDao.update(entity) } returns Unit

        // When

        val result: String = repository.updateBrand(brandModel)

        // Then
        assertEquals("Marca actualizada exitosamente", result)
        coVerify(exactly = 1) { brandDao.update(entity) }
    }

    @Test
    fun `updateBrand should return error message when an exception occurs`() = runTest {

        // Given
        val brandModel = BrandDomainModel(brandId = 1L, brandName = "Yebram")
        val entity = MarcaEntity(id = 1L, nombre = "Yebram")

        coEvery { brandDao.update(entity) } throws RuntimeException("Simulated error")

        // When

        val result: String = repository.updateBrand(brandModel)

        // Then
        assertEquals("Error: Simulated error", result)
        coVerify(exactly = 1) { brandDao.update(entity) }
    }

}