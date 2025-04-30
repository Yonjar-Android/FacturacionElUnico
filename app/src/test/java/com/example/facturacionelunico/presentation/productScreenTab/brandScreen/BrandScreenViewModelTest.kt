package com.example.facturacionelunico.presentation.productScreenTab.brandScreen

import app.cash.turbine.turbineScope
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.BrandRepository
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
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class BrandScreenViewModelTest {

    @MockK
    lateinit var repository: BrandRepository

    lateinit var viewModel: BrandScreenViewModel

    @get:Rule
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = BrandScreenViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateQuery should update searchQuery`() = runTest {
        val newQuery = "New Query"
        viewModel.updateQuery(newQuery)
        assertTrue(viewModel.searchQuery.value == newQuery)
    }

    @Test
    fun `createBrand should update message`() = runTest {
        // Given
        val marca = "Golden Boy"
        coEvery { repository.createBrand(marca) } returns "Marca creada exitosamente"

        turbineScope {
         val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial State
            assertTrue(turbineMessage.awaitItem() == null)


            // Call the function to create the brand
            viewModel.createBrand(marca)

            // Second State
            assertTrue(turbineMessage.awaitItem() == "Marca creada exitosamente")

            turbineMessage.cancel()
        }

        coVerify(exactly = 1) { repository.createBrand(marca) }
    }

    @Test
    fun `brands should return all brands when query is empty`() = runTest {
        // Given
        val mockBrands = listOf(BrandDomainModel(1, "Brand1"), BrandDomainModel(2, "Brand2"))
        coEvery { repository.getBrands() } returns flow { emit(ResultPattern.Success(mockBrands)) }

        turbineScope {
            val turbineBrands = viewModel.brands.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<BrandDomainModel>(), turbineBrands.awaitItem())

            // Update query to empty string
            viewModel.updateQuery("")
            testDispatcher.scheduler.advanceUntilIdle()

            // Should return all brands
            assertEquals(mockBrands, turbineBrands.awaitItem())

            turbineBrands.cancel()
        }
        coVerify(exactly = 1) { repository.getBrands() }
    }

    @Test
    fun `brands should return filtered brands when query is not empty`() = runTest {
        // Given
        val query = "Brand1"
        val mockBrand = listOf(BrandDomainModel(1, "Brand1"))
        coEvery { repository.getBrandByName(query) } returns flow { emit(ResultPattern.Success(mockBrand)) }

        turbineScope {
            val turbineBrands = viewModel.brands.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<BrandDomainModel>(), turbineBrands.awaitItem())

            // Update query
            viewModel.updateQuery(query)
            testDispatcher.scheduler.advanceUntilIdle() // Wait for debounce

            // Should return filtered brands
            val result = turbineBrands.awaitItem()
            assertEquals(mockBrand, result)
            assertTrue(result.first().brandName.contains(query))

            turbineBrands.cancel()
        }

        coVerify(exactly = 1) { repository.getBrandByName(query) }
    }

    @Test
    fun `brands should handle error and update message`() = runTest {
        // Given
        val query = "Invalid"
        val errorMessage = "Error fetching brands"
        coEvery { repository.getBrandByName(query) } returns flow { emit(ResultPattern.Error(message = errorMessage)) }

        turbineScope {
            val turbineBrands = viewModel.brands.testIn(backgroundScope)
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial states
            assertEquals(emptyList<BrandDomainModel>(), turbineBrands.awaitItem())
            assertEquals(null, turbineMessage.awaitItem())

            // Update query
            viewModel.updateQuery(query)
            testDispatcher.scheduler.advanceUntilIdle()

            // Should return empty list and update message
            assertEquals(errorMessage, turbineMessage.awaitItem())

            turbineBrands.cancel()
            turbineMessage.cancel()
        }
        coVerify(exactly = 1) { repository.getBrandByName(query) }
    }

    @Test
    fun `restartMessage should reset message to null`() = runTest {
        // Given
        val marca = "Test Brand"
        coEvery { repository.createBrand(marca) } returns "Success message"

        turbineScope {
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial state
            assertEquals(null, turbineMessage.awaitItem())

            // Create brand to set message
            viewModel.createBrand(marca)
            assertEquals("Success message", turbineMessage.awaitItem())

            // Restart message
            viewModel.restartMessage()
            assertEquals(null, turbineMessage.awaitItem())

            turbineMessage.cancel()
        }
        coVerify(exactly = 1) { repository.createBrand(marca) }
    }

    @Test
    fun `brands should debounce queries`() = runTest {
        // Given
        val mockBrands = listOf(BrandDomainModel(1, "Brand1"))
        coEvery { repository.getBrandByName("Brand1") } returns flow { emit(ResultPattern.Success(mockBrands)) }

        turbineScope {
            val turbineBrands = viewModel.brands.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<BrandDomainModel>(), turbineBrands.awaitItem())

            // Rapid successive queries
            viewModel.updateQuery("B")
            viewModel.updateQuery("Br")
            // ... más updates
            viewModel.updateQuery("Brand1")

            // Avanzar menos que el debounce
            testDispatcher.scheduler.advanceTimeBy(200)
            turbineBrands.expectNoEvents()

            // Avanzar pasado el debounce
            testDispatcher.scheduler.advanceTimeBy(200)
            assertEquals(mockBrands, turbineBrands.awaitItem())

            turbineBrands.cancel()
        }
    }

}