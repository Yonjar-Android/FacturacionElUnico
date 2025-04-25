package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.CategoriaDao
import com.example.facturacionelunico.data.database.entities.CategoriaEntity
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.CategoryRepository
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

class CategoryRepositoryImpTest {

    @MockK
    lateinit var categoryDao: CategoriaDao

    lateinit var repository: CategoryRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = CategoryRepositoryImp(categoryDao)
    }

    @Test
    fun `getCategories should return Success with mapped data`() = runTest {

        // Given
        val response = MotherObjectRepositories.flowCategoryEntities
        every { categoryDao.getAll() } returns response

        // When

        val resultFlow: Flow<ResultPattern<List<CategoryDomainModel>>> = repository.getCategories()
        val results = mutableListOf<ResultPattern<List<CategoryDomainModel>>>()

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
            assertEquals(expectedBrands.first().id, data[0].categoryId)
            assertEquals(expectedBrands.first().nombre, data[0].categoryName)
        }
    }

    @Test
    fun `getCategories should return Success with no data`() = runTest {

        // Given
        val response = flow { emit(emptyList<CategoriaEntity>()) }
        every { categoryDao.getAll() } returns response

        // When

        val resultFlow: Flow<ResultPattern<List<CategoryDomainModel>>> = repository.getCategories()
        val results = mutableListOf<ResultPattern<List<CategoryDomainModel>>>()

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
    fun `getCategories should return Error when an exception occurs`() = runTest {

        // Given
        val exception = RuntimeException("Simulated error")
        every { categoryDao.getAll() } returns flow { throw exception }

        // When

        val resultFlow: Flow<ResultPattern<List<CategoryDomainModel>>> = repository.getCategories()
        val results = mutableListOf<ResultPattern<List<CategoryDomainModel>>>()

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
    fun `getCategoryByName should return Success with mapped data`() = runTest {

        // Given
        val response = flow { emit(listOf<CategoriaEntity>(MotherObjectRepositories.categoriaLLantas)) }
        val search = "Lla"
        every { categoryDao.getCategoryByName(search) } returns response

        // When

        val resultFlow: Flow<ResultPattern<List<CategoryDomainModel>>> = repository.getCategoryByName(search)
        val results = mutableListOf<ResultPattern<List<CategoryDomainModel>>>()

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
            assertEquals(expectedBrands.first().id, data[0].categoryId)
            assertEquals(expectedBrands.first().nombre, data[0].categoryName)
            assertTrue(data[0].categoryName.contains(search))
        }
    }

    @Test
    fun `getCategoryByName should return Success with no data`() = runTest {

        // Given
        val response = flowOf(emptyList<CategoriaEntity>())
        val search = "Ni"
        every { categoryDao.getCategoryByName(search) } returns response

        // When

        val resultFlow: Flow<ResultPattern<List<CategoryDomainModel>>> = repository.getCategoryByName(search)
        val results = mutableListOf<ResultPattern<List<CategoryDomainModel>>>()

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
    fun `getCategoryByName should return Error when an exception occurs`() = runTest {

        // Given
        val exception = RuntimeException("Simulated error")
        val search = "Ni"

        every { categoryDao.getCategoryByName(search) } returns flow { throw exception }

        // When

        val resultFlow: Flow<ResultPattern<List<CategoryDomainModel>>> = repository.getCategoryByName(search)
        val results = mutableListOf<ResultPattern<List<CategoryDomainModel>>>()

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
    fun `getCategoryById should return Success with mapped data`() = runTest {

        // Given
        val brandFlow = MotherObjectRepositories.flowOneCategory
        val id = 1L

        coEvery { categoryDao.getCategoryById(id) } returns brandFlow

        // When

        val resultFlow: Flow<CategoryDomainModel> = repository.getCategoryById(id)
        val result = resultFlow.first()
        val response = brandFlow.first()

        // Then
        assertEquals(result.categoryId, response.id)
        assertEquals(result.categoryId, id)
        coVerify(exactly = 1) { categoryDao.getCategoryById(id) }
    }

    @Test
    fun `getProductsByCategory should return Success with mapped data`() = runTest {

        // Given
        val products = MotherObjectRepositories.products
        val id = 1L

        coEvery { categoryDao.getDetailedByCategoryId(id) } returns products

        // When

        val result: List<DetailedProductModel> = repository.getProductsByCategory(id)

        // Then
        assertEquals(products, result)
        assertEquals(products[0].categoryId, id)
        coVerify(exactly = 1) { categoryDao.getDetailedByCategoryId(id) }
    }

    @Test
    fun `createCategory should return success message when category is created successfully`() = runTest {

        // Given
        val name = "Neumaticos"
        val entity = CategoriaEntity(nombre = name)
        coEvery { categoryDao.insert(entity) } returns Unit

        // When

        val result: String = repository.createCategory(name)

        // Then
        assertEquals("Categoría creada exitosamente", result)
        coVerify(exactly = 1) { categoryDao.insert(entity) }
    }

    @Test
    fun `createCategory should return error message when an exception occurs`() = runTest {

        // Given
        val name = "Neumaticos"
        val entity = CategoriaEntity(nombre = name)

        coEvery { categoryDao.insert(entity) } throws RuntimeException("Simulated error")

        // When

        val result: String = repository.createCategory(name)

        // Then
        assertEquals("Error: Simulated error", result)
        coVerify(exactly = 1) { categoryDao.insert(entity) }
    }

    @Test
    fun `updateCategory should return success message when brand is updated successfully`() = runTest {

        // Given
        val categoryModel = CategoryDomainModel(categoryId = 1L, categoryName = "Neumaticos")
        val entity = CategoriaEntity(id = 1L, nombre = "Neumaticos")

        coEvery { categoryDao.update(entity) } returns Unit

        // When

        val result: String = repository.updateCategory(categoryModel)

        // Then
        assertEquals("Categoría actualizada exitosamente", result)
        coVerify(exactly = 1) { categoryDao.update(entity) }
    }

    @Test
    fun `updateCategory should return error message when an exception occurs`() = runTest {

        // Given
        val brandModel = CategoryDomainModel(categoryId = 1L, categoryName = "Neumaticos")
        val entity = CategoriaEntity(id = 1L, nombre = "Neumaticos")

        coEvery { categoryDao.update(entity) } throws RuntimeException("Simulated error")

        // When

        val result: String = repository.updateCategory(brandModel)

        // Then
        assertEquals("Error: Simulated error", result)
        coVerify(exactly = 1) { categoryDao.update(entity) }
    }

}