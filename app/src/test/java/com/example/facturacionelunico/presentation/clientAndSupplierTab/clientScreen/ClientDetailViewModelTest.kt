package com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen

import app.cash.turbine.turbineScope
import com.example.facturacionelunico.data.repositories.motherObjects.MotherObjectClientSuppliers
import com.example.facturacionelunico.domain.repositories.ClientRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
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
class ClientDetailViewModelTest {
    @MockK
    lateinit var repository: ClientRepository

    lateinit var viewModel: ClientDetailViewModel

    @get:Rule
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ClientDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateClient should update  message state`() = runTest{
        //Given
        val domain = MotherObjectClientSuppliers.oneDomainClient
        coEvery { repository.updateClient(domain) } returns "Success message"

        turbineScope { val turbineMessage = viewModel.message.testIn(backgroundScope)

            assertEquals(null, turbineMessage.awaitItem())

            //When
            viewModel.updateClient(domain)

            assertEquals("Success message", turbineMessage.awaitItem())

            //Then

            turbineMessage.cancel()
        }

        coVerify(exactly = 1) { repository.updateClient(domain) }

    }

    @Test
    fun `getClientById should update supplier state`() = runTest {
        // Given
        val id = 1L
        val domain = MotherObjectClientSuppliers.oneDomainClient
        coEvery { repository.getClientById(id) } returns flow { emit(domain) }

        turbineScope {
            val turbineBrand = viewModel.client.testIn(backgroundScope)

            assertEquals(null, turbineBrand.awaitItem())
            // When
            viewModel.getClientById(id)

            assertEquals(domain, turbineBrand.awaitItem())
            turbineBrand.cancel()
        }

        coVerify(exactly = 1) { repository.getClientById(id) }
    }

    @Test
    fun `restartMessage should reset message to null`() = runTest {
        // Given
        val domain = MotherObjectClientSuppliers.oneDomainClient
        coEvery { repository.updateClient(domain) } returns "Success message"

        turbineScope {
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial state
            assertEquals(null, turbineMessage.awaitItem())

            // Set message
            viewModel.updateClient(domain)
            assertEquals("Success message", turbineMessage.awaitItem())

            // Reset message
            viewModel.restartMessage()
            assertEquals(null, turbineMessage.awaitItem())

            turbineMessage.cancel()
        }
    }

    @Test
    fun `getClientById should handle empty flow`() = runTest {
        // Given
        val id = 1L
        coEvery { repository.getClientById(id) } returns flow { /* Empty flow */ }

        turbineScope {
            val turbineBrand = viewModel.client.testIn(backgroundScope)

            viewModel.getClientById(id)

            // Should remain null since flow is empty
            assertEquals(null, turbineBrand.awaitItem())
            turbineBrand.cancel()
        }
    }
}