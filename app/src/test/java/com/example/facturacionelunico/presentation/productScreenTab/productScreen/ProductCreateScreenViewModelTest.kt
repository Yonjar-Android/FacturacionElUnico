package com.example.facturacionelunico.presentation.productScreenTab.productScreen

import app.cash.turbine.turbineScope
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.BrandRepository
import com.example.facturacionelunico.domain.repositories.CategoryRepository
import com.example.facturacionelunico.domain.repositories.ProductRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductCreateScreenViewModelTest {

    @MockK
    lateinit var repository: ProductRepository

    @MockK
    lateinit var repositoryBrand: BrandRepository

    @MockK
    lateinit var repositoryCategory: CategoryRepository

    lateinit var viewModel: ProductCreateScreenViewModel

    val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ProductCreateScreenViewModel(repository, repositoryBrand, repositoryCategory)
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }

    @Test
    fun `createProduct should update message`() = runTest {
        // Given
        val product = MotherObjectProduct.product
        coEvery { repository.createProduct(product) } returns "Producto creado exitosamente"

        turbineScope {
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial State
            assertTrue(turbineMessage.awaitItem() == null)

            // Call the function to create the category
            viewModel.createProduct(product)

            // Second State
            assertTrue(turbineMessage.awaitItem() == "Producto creado exitosamente")

            turbineMessage.cancel()
        }

        coVerify(exactly = 1) { repository.createProduct(product) }
    }

    @Test
    fun `updateQueryBrand should update searchQueryBrand`() = runTest {
        val newQuery = "New Query"
        viewModel.updateQueryBrand(newQuery)
        assertTrue(viewModel.searchQueryBrand.value == newQuery)
    }

    @Test
    fun `updateQueryCategory should update searchQueryCategory`() = runTest {
        val newQuery = "New Query"
        viewModel.updateQueryCategory(newQuery)
        assertTrue(viewModel.searchQueryCategory.value == newQuery)
    }

    @Test
    fun `brands should return all brands when query is empty`() = runTest {
        // Given
        val mockBrands = listOf(BrandDomainModel(1, "Brand1"), BrandDomainModel(2, "Brand2"))
        coEvery { repositoryBrand.getBrands() } returns flow { emit(ResultPattern.Success(mockBrands)) }

        turbineScope {
            val turbineBrands = viewModel.brands.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<BrandDomainModel>(), turbineBrands.awaitItem())

            // Update query to empty string
            viewModel.updateQueryBrand("")
            testDispatcher.scheduler.advanceUntilIdle()

            // Should return all brands
            assertEquals(mockBrands, turbineBrands.awaitItem())

            turbineBrands.cancel()
        }
        coVerify(exactly = 1) { repositoryBrand.getBrands() }
    }

    @Test
    fun `brands should return filtered brands when query is not empty`() = runTest {
        // Given
        val query = "Brand1"
        val mockBrand = listOf(BrandDomainModel(1, "Brand1"))
        coEvery { repositoryBrand.getBrandByName(query) } returns flow { emit(ResultPattern.Success(mockBrand)) }

        turbineScope {
            val turbineBrands = viewModel.brands.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<BrandDomainModel>(), turbineBrands.awaitItem())

            // Update query
            viewModel.updateQueryBrand(query)
            testDispatcher.scheduler.advanceUntilIdle() // Wait for debounce

            // Should return filtered brands
            val result = turbineBrands.awaitItem()
            assertEquals(mockBrand, result)
            assertTrue(result.first().brandName.contains(query))

            turbineBrands.cancel()
        }

        coVerify(exactly = 1) { repositoryBrand.getBrandByName(query) }
    }

    @Test
    fun `brands should handle error and update message`() = runTest {
        // Given
        val query = "Invalid"
        val errorMessage = "Error fetching brands"
        coEvery { repositoryBrand.getBrandByName(query) } returns flow { emit(ResultPattern.Error(message = errorMessage)) }

        turbineScope {
            val turbineBrands = viewModel.brands.testIn(backgroundScope)
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial states
            assertEquals(emptyList<BrandDomainModel>(), turbineBrands.awaitItem())
            assertEquals(null, turbineMessage.awaitItem())

            // Update query
            viewModel.updateQueryBrand(query)
            testDispatcher.scheduler.advanceUntilIdle()

            // Should return empty list and update message
            assertEquals(errorMessage, turbineMessage.awaitItem())

            turbineBrands.cancel()
            turbineMessage.cancel()
        }
        coVerify(exactly = 1) { repositoryBrand.getBrandByName(query) }
    }

    @Test
    fun `getCategories should return all categories when query is empty`() = runTest {
        // Given
        val mockCategories = listOf(CategoryDomainModel(1, "Category1"), CategoryDomainModel(2, "Category2"))
        coEvery { repositoryCategory.getCategories() } returns flow { emit(ResultPattern.Success(mockCategories)) }

        turbineScope {
            val turbineCategories = viewModel.categories.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<CategoryDomainModel>(), turbineCategories.awaitItem())

            // Update query to empty string
            viewModel.updateQueryCategory("")
            testDispatcher.scheduler.advanceUntilIdle()

            // Should return all categories
            assertEquals(mockCategories, turbineCategories.awaitItem())

            turbineCategories.cancel()
        }
        coVerify(exactly = 1) { repositoryCategory.getCategories() }
    }

    @Test
    fun `getCategories should return filtered categories when query is not empty`() = runTest {
        // Given
        val query = "Category1"
        val mockCategories = listOf(CategoryDomainModel(1, "Category1"))
        coEvery { repositoryCategory.getCategoryByName(query) } returns flow { emit(ResultPattern.Success(mockCategories)) }

        turbineScope {
            val turbineCategories = viewModel.categories.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<CategoryDomainModel>(), turbineCategories.awaitItem())

            // Update query
            viewModel.updateQueryCategory(query)
            testDispatcher.scheduler.advanceUntilIdle() // Wait for debounce

            // Should return filtered categories
            val result = turbineCategories.awaitItem()
            assertEquals(mockCategories, result)
            assertTrue(result.first().categoryName.contains(query))

            turbineCategories.cancel()
        }

        coVerify(exactly = 1) { repositoryCategory.getCategoryByName(query) }
    }

    @Test
    fun `categories should handle error and update message`() = runTest {
        // Given
        val query = "Invalid"
        val errorMessage = "Error fetching categories"
        coEvery { repositoryCategory.getCategoryByName(query) } returns flow { emit(ResultPattern.Error(message = errorMessage)) }

        turbineScope {
            val turbineCategories= viewModel.categories.testIn(backgroundScope)
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial states
            assertEquals(emptyList<CategoryDomainModel>(), turbineCategories.awaitItem())
            assertEquals(null, turbineMessage.awaitItem())

            // Update query
            viewModel.updateQueryCategory(query)
            testDispatcher.scheduler.advanceUntilIdle()

            // Should return empty list and update message
            assertEquals(errorMessage, turbineMessage.awaitItem())

            turbineCategories.cancel()
            turbineMessage.cancel()
        }
        coVerify(exactly = 1) { repositoryCategory.getCategoryByName(query) }
    }

}