package com.example.facturacionelunico.presentation.productScreenTab.productScreen

import app.cash.turbine.turbineScope
import com.example.facturacionelunico.data.repositories.MotherObjectRepositories
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ProductDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
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
class ProductScreenViewModelTest {

    @MockK
    lateinit var repository: ProductRepository

    lateinit var viewModel: ProductScreenViewModel

    val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ProductScreenViewModel(repository)
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }

    @Test
    fun `updateQuery should update searchQuery`() = runTest {
        val newQuery = "New Query"
        viewModel.updateQuery(newQuery)
        assertTrue(viewModel.searchQuery.value == newQuery)
    }

    @Test
    fun `getProducts should return all products when query is empty`() = runTest {
        // Given
        val mockProducts = MotherObjectRepositories.products
        coEvery { repository.getProducts() } returns flow { emit(ResultPattern.Success(mockProducts)) }

        turbineScope {
            val turbineProducts = viewModel.products.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<BrandDomainModel>(), turbineProducts.awaitItem())

            // Update query to empty string
            viewModel.updateQuery("")

            // Should return all products
            assertEquals(mockProducts, turbineProducts.awaitItem())

            turbineProducts.cancel()
        }
        coVerify(exactly = 1) { repository.getProducts() }
    }

    @Test
    fun `getProducts should return filtered categories when query is not empty`() = runTest {
        // Given
        val query = "Kenda"
        val mockProducts = MotherObjectRepositories.products
        coEvery { repository.getProductBySearch(query) } returns flow { emit(ResultPattern.Success(mockProducts)) }

        turbineScope {
            val turbineProducts = viewModel.products.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<BrandDomainModel>(), turbineProducts.awaitItem())

            // Update query
            viewModel.updateQuery(query)

            // Should return filtered brands
            val result = turbineProducts.awaitItem()
            assertEquals(mockProducts, result)
            assertTrue(result.first().name.contains(query))

            turbineProducts.cancel()
        }

        coVerify(exactly = 1) { repository.getProductBySearch(query) }
    }

    @Test
    fun `products should handle error and update message`() = runTest {
        // Given
        val query = "Invalid"
        val errorMessage = "Error fetching products"
        coEvery { repository.getProductBySearch(query) } returns flow { emit(ResultPattern.Error(message = errorMessage)) }

        turbineScope {
            val turbineProducts= viewModel.products.testIn(backgroundScope)
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial states
            assertEquals(emptyList<ProductDomainModel>(), turbineProducts.awaitItem())
            assertEquals(null, turbineMessage.awaitItem())

            // Update query
            viewModel.updateQuery(query)

            // Should return empty list and update message
            assertEquals(errorMessage, turbineMessage.awaitItem())

            turbineProducts.cancel()
            turbineMessage.cancel()
        }
        coVerify(exactly = 1) { repository.getProductBySearch(query) }
    }

    @Test
    fun `products should debounce queries`() = runTest(testDispatcher) {
        // Given
        val mockProducts = listOf(MotherObjectRepositories.productDetailed)
        coEvery { repository.getProductBySearch("Kenda") } returns flow { emit(ResultPattern.Success(mockProducts)) }

        turbineScope {
            val turbineProducts = viewModel.products.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<DetailedProductModel>(), turbineProducts.awaitItem())

            // Rapid successive queries
            viewModel.updateQuery("K")
            viewModel.updateQuery("Ke")
            // ... más updates
            viewModel.updateQuery("Kenda")

            // Avanzar menos que el debounce
            testDispatcher.scheduler.advanceTimeBy(200)
            turbineProducts.expectNoEvents()

            // Avanzar pasado el debounce
            testDispatcher.scheduler.advanceTimeBy(200)
            assertEquals(mockProducts, turbineProducts.awaitItem())

            turbineProducts.cancel()
        }
    }

}