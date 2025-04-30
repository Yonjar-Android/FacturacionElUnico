package com.example.facturacionelunico.presentation.productScreenTab.productScreen.productDetailScreen

import app.cash.turbine.turbineScope
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.ProductRepository
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.MotherObjectProduct
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.ProductScreenViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


class ProductDetailViewModelTest {

    @MockK
    lateinit var repository: ProductRepository

    lateinit var viewModel: ProductDetailViewModel

    val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = ProductDetailViewModel(repository)
    }

        @Test
        fun `loadProduct should update product state when successful`() = runTest {
            // Given
            val productId = 201L
            val mockProduct = MotherObjectProduct.productDetailed
            coEvery { repository.getProductById(productId) } returns ResultPattern.Success(mockProduct)

            turbineScope {
                val turbineProduct = viewModel.product.testIn(backgroundScope)
                val turbineMessage = viewModel.message.testIn(backgroundScope)

                // Initial state
                assertEquals(null, turbineProduct.awaitItem())
                assertEquals(null, turbineMessage.awaitItem())

                // When
                viewModel.loadProduct(productId)

                // Then
                val result = turbineProduct.awaitItem()
                assertEquals(mockProduct, result)
                assertEquals(productId, result?.id)

                turbineProduct.cancel()
                turbineMessage.cancel()
            }

            coVerify(exactly = 1) { repository.getProductById(productId) }
        }

        @Test
        fun `loadProduct should update message state when error occurs`() = runTest {
            // Given
            val productId = 201L
            val errorMessage = "Product not found"
            coEvery { repository.getProductById(productId) } returns ResultPattern.Error(null,errorMessage)

            turbineScope {
                val turbineProduct = viewModel.product.testIn(backgroundScope)
                val turbineMessage = viewModel.message.testIn(backgroundScope)

                assertEquals(null, turbineMessage.awaitItem())
                assertEquals(null, turbineProduct.awaitItem())

                // When
                viewModel.loadProduct(productId)

                // Then
                assertEquals(errorMessage, turbineMessage.awaitItem())

                turbineProduct.cancel()
                turbineMessage.cancel()
            }

            coVerify(exactly = 1) { repository.getProductById(productId) }
        }

        @Test
        fun `restartMessage should reset message to null`() = runTest {
            // Given
            val productId = 1L
            val errorMessage = "Error message"
            coEvery { repository.getProductById(productId) } returns ResultPattern.Error(null,errorMessage)

            turbineScope {
                val turbineMessage = viewModel.message.testIn(backgroundScope)

                assertEquals(null, turbineMessage.awaitItem())

                // First set a message
                viewModel.loadProduct(productId)
                assertEquals(errorMessage, turbineMessage.awaitItem())

                // When
                viewModel.restartMessage()

                // Then
                assertEquals(null, turbineMessage.awaitItem())

                turbineMessage.cancel()
            }
        }
    }
