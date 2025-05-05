package com.example.facturacionelunico.presentation.clientAndSupplierTab.supplierScreen

import app.cash.turbine.turbineScope
import com.example.facturacionelunico.data.repositories.motherObjects.MotherObjectClientSuppliers
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.SupplierDomainModel
import com.example.facturacionelunico.domain.repositories.SupplierRepository
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
class SupplierScreenViewModelTest {
    @MockK
    lateinit var repository: SupplierRepository

    lateinit var viewModel: SupplierScreenViewModel

    @get:Rule
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = SupplierScreenViewModel(repository)
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
    fun `createSupplier should update message`() = runTest {
        // Given
        val supplier = MotherObjectClientSuppliers.oneDomainSupplier
        coEvery { repository.createSupplier(supplier) } returns "Se ha creado un nuevo proveedor"

        turbineScope {
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial State
            assertTrue(turbineMessage.awaitItem() == null)


            // Call the function to create the brand
            viewModel.createSupplier(supplier)

            // Second State
            assertTrue(turbineMessage.awaitItem() == "Se ha creado un nuevo proveedor")

            turbineMessage.cancel()
        }

        coVerify(exactly = 1) { repository.createSupplier(supplier) }
    }

    @Test
    fun `getSuppliers should return all suppliers when query is empty`() = runTest {
        // Given
        val suppliers = MotherObjectClientSuppliers.listDomainSupplier
        coEvery { repository.getSuppliers() } returns flow { emit(ResultPattern.Success(suppliers)) }

        turbineScope {
            val turbineSuppliers = viewModel.suppliers.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<BrandDomainModel>(), turbineSuppliers.awaitItem())

            // Update query to empty string
            viewModel.updateQuery("")
            testDispatcher.scheduler.advanceUntilIdle()

            // Should return all brands
            assertEquals(suppliers, turbineSuppliers.awaitItem())

            turbineSuppliers.cancel()
        }
        coVerify(exactly = 1) { repository.getSuppliers() }
    }

    @Test
    fun `getSuppliersBySearch should return filtered suppliers when query is not empty`() = runTest {
        // Given
        val query = "Kenda"
        val suppliers = MotherObjectClientSuppliers.listDomainSupplier
        coEvery { repository.getSuppliersBySearch(query) } returns flow { emit(ResultPattern.Success(suppliers)) }

        turbineScope {
            val turbineSuppliers = viewModel.suppliers.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<SupplierDomainModel>(), turbineSuppliers.awaitItem())

            // Update query
            viewModel.updateQuery(query)
            testDispatcher.scheduler.advanceUntilIdle() // Wait for debounce

            // Should return filtered brands
            val result = turbineSuppliers.awaitItem()
            assertEquals(suppliers, result)
            assertTrue(result.first().company.contains(query))

            turbineSuppliers.cancel()
        }

        coVerify(exactly = 1) { repository.getSuppliersBySearch(query) }
    }

    @Test
    fun `getSuppliersBySearch should handle error and update message`() = runTest {
        // Given
        val query = "Invalid"
        val errorMessage = "Error fetching brands"
        coEvery { repository.getSuppliersBySearch(query) } returns flow { emit(ResultPattern.Error(message = errorMessage)) }

        turbineScope {
            val turbineSuppliers = viewModel.suppliers.testIn(backgroundScope)
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial states
            assertEquals(emptyList<SupplierDomainModel>(), turbineSuppliers.awaitItem())
            assertEquals(null, turbineMessage.awaitItem())

            // Update query
            viewModel.updateQuery(query)
            testDispatcher.scheduler.advanceUntilIdle()

            // Should return empty list and update message
            assertEquals(errorMessage, turbineMessage.awaitItem())

            turbineSuppliers.cancel()
            turbineMessage.cancel()
        }
        coVerify(exactly = 1) { repository.getSuppliersBySearch(query) }
    }

    @Test
    fun `restartMessage should reset message to null`() = runTest {
        // Given
        val supplier = MotherObjectClientSuppliers.oneDomainSupplier
        coEvery { repository.createSupplier(supplier) } returns "Success message"

        turbineScope {
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial state
            assertEquals(null, turbineMessage.awaitItem())

            // Create brand to set message
            viewModel.createSupplier(supplier)
            assertEquals("Success message", turbineMessage.awaitItem())

            // Restart message
            viewModel.restartMessage()
            assertEquals(null, turbineMessage.awaitItem())

            turbineMessage.cancel()
        }
        coVerify(exactly = 1) { repository.createSupplier(supplier) }
    }

    @Test
    fun `getSuppliersBySearch should debounce queries`() = runTest {
        // Given
        val suppliers = MotherObjectClientSuppliers.listDomainSupplier
        coEvery { repository.getSuppliersBySearch("Kenda") } returns flow { emit(ResultPattern.Success(suppliers)) }

        turbineScope {
            val turbineBrands = viewModel.suppliers.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<BrandDomainModel>(), turbineBrands.awaitItem())

            // Rapid successive queries
            viewModel.updateQuery("K")
            viewModel.updateQuery("Ke")
            // ... más updates
            viewModel.updateQuery("Kenda")

            // Avanzar menos que el debounce
            testDispatcher.scheduler.advanceTimeBy(200)
            turbineBrands.expectNoEvents()

            // Avanzar pasado el debounce
            testDispatcher.scheduler.advanceTimeBy(200)
            assertEquals(suppliers, turbineBrands.awaitItem())

            turbineBrands.cancel()
        }
    }

}