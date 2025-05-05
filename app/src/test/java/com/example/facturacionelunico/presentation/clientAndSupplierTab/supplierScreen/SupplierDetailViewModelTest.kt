package com.example.facturacionelunico.presentation.clientAndSupplierTab.supplierScreen

import app.cash.turbine.turbineScope
import com.example.facturacionelunico.data.repositories.motherObjects.MotherObjectClientSuppliers
import com.example.facturacionelunico.domain.repositories.SupplierRepository
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
class SupplierDetailViewModelTest {
    @MockK
    lateinit var repository: SupplierRepository

    lateinit var viewModel: SupplierDetailViewModel

    @get:Rule
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = SupplierDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateSupplier should update  message state`() = runTest{
        //Given
        val domain = MotherObjectClientSuppliers.oneDomainSupplier
        coEvery { repository.updateSupplier(domain) } returns "Success message"

        turbineScope { val turbineMessage = viewModel.message.testIn(backgroundScope)

            assertEquals(null, turbineMessage.awaitItem())

            //When
            viewModel.updateSupplier(domain)

            assertEquals("Success message", turbineMessage.awaitItem())

            //Then

            turbineMessage.cancel()
        }

        coVerify(exactly = 1) { repository.updateSupplier(domain) }

    }

    @Test
    fun `getSupplierById should update supplier state`() = runTest {
        // Given
        val id = 1L
        val domain = MotherObjectClientSuppliers.oneDomainSupplier
        coEvery { repository.getSupplierById(id) } returns flow { emit(domain) }

        turbineScope {
            val turbineBrand = viewModel.supplier.testIn(backgroundScope)

            assertEquals(null, turbineBrand.awaitItem())
            // When
            viewModel.getSupplierById(id)

            assertEquals(domain, turbineBrand.awaitItem())
            turbineBrand.cancel()
        }

        coVerify(exactly = 1) { repository.getSupplierById(id) }
    }

    @Test
    fun `restartMessage should reset message to null`() = runTest {
        // Given
        val domain = MotherObjectClientSuppliers.oneDomainSupplier
        coEvery { repository.updateSupplier(domain) } returns "Success message"

        turbineScope {
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial state
            assertEquals(null, turbineMessage.awaitItem())

            // Set message
            viewModel.updateSupplier(domain)
            assertEquals("Success message", turbineMessage.awaitItem())

            // Reset message
            viewModel.restartMessage()
            assertEquals(null, turbineMessage.awaitItem())

            turbineMessage.cancel()
        }
    }

    @Test
    fun `getSupplierById should handle empty flow`() = runTest {
        // Given
        val id = 1L
        coEvery { repository.getSupplierById(id) } returns flow { /* Empty flow */ }

        turbineScope {
            val turbineBrand = viewModel.supplier.testIn(backgroundScope)

            viewModel.getSupplierById(id)

            // Should remain null since flow is empty
            assertEquals(null, turbineBrand.awaitItem())
            turbineBrand.cancel()
        }
    }
}