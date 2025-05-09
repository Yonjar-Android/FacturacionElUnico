package com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen

import app.cash.turbine.turbineScope
import com.example.facturacionelunico.data.repositories.motherObjects.MotherObjectClientSuppliers
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.client.ClientDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.SupplierDomainModel
import com.example.facturacionelunico.domain.repositories.ClientRepository
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
class ClientScreenViewModelTest {
    @MockK
    lateinit var repository: ClientRepository

    lateinit var viewModel: ClientScreenViewModel

    @get:Rule
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ClientScreenViewModel(repository)
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
        val client = MotherObjectClientSuppliers.oneDomainClient
        coEvery { repository.createClient(client) } returns "Se ha creado un nuevo cliente"

        turbineScope {
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial State
            assertTrue(turbineMessage.awaitItem() == null)


            // Call the function to create the brand
            viewModel.createClient(client)

            // Second State
            assertTrue(turbineMessage.awaitItem() == "Se ha creado un nuevo cliente")

            turbineMessage.cancel()
        }

        coVerify(exactly = 1) { repository.createClient(client) }
    }

    @Test
    fun `getClients should return all clients when query is empty`() = runTest {
        // Given
        val clients = MotherObjectClientSuppliers.listDomainClient
        coEvery { repository.getClients() } returns flow { emit(ResultPattern.Success(clients)) }

        turbineScope {
            val turbineSuppliers = viewModel.clients.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<BrandDomainModel>(), turbineSuppliers.awaitItem())

            // Update query to empty string
            viewModel.updateQuery("")
            testDispatcher.scheduler.advanceUntilIdle()

            // Should return all brands
            assertEquals(clients, turbineSuppliers.awaitItem())

            turbineSuppliers.cancel()
        }
        coVerify(exactly = 1) { repository.getClients() }
    }

    @Test
    fun `getClientsBySearch should return filtered clients when query is not empty`() = runTest {
        // Given
        val query = "José"
        val clients = listOf(MotherObjectClientSuppliers.oneDomainClient)
        coEvery { repository.getClientBySearch(query) } returns flow { emit(ResultPattern.Success(clients)) }

        turbineScope {
            val turbineClients = viewModel.clients.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<ClientDomainModel>(), turbineClients.awaitItem())

            // Update query
            viewModel.updateQuery(query)
            testDispatcher.scheduler.advanceUntilIdle() // Wait for debounce

            // Should return brands
            val result = turbineClients.awaitItem()
            assertEquals(clients, result)
            assertTrue(result.first().name.contains(query))

            turbineClients.cancel()
        }

        coVerify(exactly = 1) { repository.getClientBySearch(query) }
    }

    @Test
    fun `getClientsBySearch should handle error and update message`() = runTest {
        // Given
        val query = "Invalid"
        val errorMessage = "Error fetching brands"
        coEvery { repository.getClientBySearch(query) } returns flow { emit(ResultPattern.Error(message = errorMessage)) }

        turbineScope {
            val turbineClients = viewModel.clients.testIn(backgroundScope)
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial states
            assertEquals(emptyList<SupplierDomainModel>(), turbineClients.awaitItem())
            assertEquals(null, turbineMessage.awaitItem())

            // Update query
            viewModel.updateQuery(query)
            testDispatcher.scheduler.advanceUntilIdle()

            // Should return empty list and update message
            assertEquals(errorMessage, turbineMessage.awaitItem())

            turbineClients.cancel()
            turbineMessage.cancel()
        }
        coVerify(exactly = 1) { repository.getClientBySearch(query) }
    }

    @Test
    fun `restartMessage should reset message to null`() = runTest {
        // Given
        val client = MotherObjectClientSuppliers.oneDomainClient
        coEvery { repository.createClient(client) } returns "Success message"

        turbineScope {
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial state
            assertEquals(null, turbineMessage.awaitItem())

            // Create brand to set message
            viewModel.createClient(client)
            assertEquals("Success message", turbineMessage.awaitItem())

            // Restart message
            viewModel.restartMessage()
            assertEquals(null, turbineMessage.awaitItem())

            turbineMessage.cancel()
        }
        coVerify(exactly = 1) { repository.createClient(client) }
    }

    @Test
    fun `getClientBySearch should debounce queries`() = runTest {
        // Given
        val clients = MotherObjectClientSuppliers.listDomainClient
        coEvery { repository.getClientBySearch("Juan") } returns flow { emit(ResultPattern.Success(clients)) }

        turbineScope {
            val turbineBrands = viewModel.clients.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<ClientDomainModel>(), turbineBrands.awaitItem())

            // Rapid successive queries
            viewModel.updateQuery("J")
            viewModel.updateQuery("Ju")
            // ... más updates
            viewModel.updateQuery("Juan")

            // Avanzar menos que el debounce
            testDispatcher.scheduler.advanceTimeBy(200)
            turbineBrands.expectNoEvents()

            // Avanzar pasado el debounce
            testDispatcher.scheduler.advanceTimeBy(200)
            assertEquals(clients, turbineBrands.awaitItem())

            turbineBrands.cancel()
        }
    }
}