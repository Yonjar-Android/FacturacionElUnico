package com.example.facturacionelunico.presentation.productScreenTab.categoryScreen.categoryDetailScreen

import app.cash.turbine.turbineScope
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.repositories.CategoryRepository
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
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class CategoryDetailScreenViewModelTest {

    @MockK
    lateinit var repository: CategoryRepository

    lateinit var viewModel: CategoryDetailScreenViewModel

    val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = CategoryDetailScreenViewModel(repository)
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }

    @Test
    fun `updateCategory should update  message state`() = runTest{
        //Given
        val categoryDomainModel = CategoryDomainModel(1, "Test Category")
        coEvery { repository.updateCategory(categoryDomainModel) } returns "Success message"

        turbineScope { val turbineMessage = viewModel.message.testIn(backgroundScope)

            assertEquals(null, turbineMessage.awaitItem())

            //When
            viewModel.updateCategory(categoryDomainModel)

            assertEquals("Success message", turbineMessage.awaitItem())

            //Then

            turbineMessage.cancel()
        }

        coVerify(exactly = 1) { repository.updateCategory(categoryDomainModel) }

    }

    @Test
    fun `observeCategory should update category state`() = runTest {
        // Given
        val id = 1L
        val mockCategory = CategoryDomainModel(id, "Test Category")
        coEvery { repository.getCategoryById(id) } returns flow { emit(mockCategory) }

        turbineScope {
            val turbineBrand = viewModel.category.testIn(backgroundScope)

            assertEquals(null, turbineBrand.awaitItem())
            // When
            viewModel.observeCategory(id)

            assertEquals(mockCategory, turbineBrand.awaitItem())
            turbineBrand.cancel()
        }

        coVerify(exactly = 1) { repository.getCategoryById(id) }
    }

    @Test
    fun `getProductsByCategory should update products state`() = runTest {
        //Given
        val categoryId = 1L
        val productDetailed = listOf<DetailedProductModel>(
            DetailedProductModel(
                id = 201,
                name = "Kenda Kaiser KR20 - Llanta 205/55R16",
                category = "Llantas para Autos",
                categoryId = 1,
                brand = "Kenda",
                brandId = 1,
                salePrice = 320.00,
                purchasePrice = 210.50,
                stock = 18,
                description = "Llanta radial premium con banda de rodadura silenciosa y eficiente en combustible. Índice de velocidad V (240 km/h)",
                photo = "https://cdn.ejemplo.com/llantas/kenda_kaiser_kr20.jpg"
            )
        )

        coEvery { repository.getProductsByCategory(categoryId) } returns productDetailed

        turbineScope {
            val turbineProducts = viewModel.products.testIn(backgroundScope)

            assertEquals(emptyList<DetailedProductModel>(), turbineProducts.awaitItem())

            viewModel.getProductsByCategory(categoryId)

            val result = turbineProducts.awaitItem()
            assertEquals(productDetailed, result)
            assertEquals(result.first().brandId, categoryId)
            turbineProducts.cancel()
        }

        coVerify(exactly = 1) { repository.getProductsByCategory(categoryId) }
    }

    @Test
    fun `restartMessage should reset message to null`() = runTest {
        // Given
        val brand = CategoryDomainModel(1, "Test Category")
        coEvery { repository.updateCategory(brand) } returns "Success message"

        turbineScope {
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial state
            assertEquals(null, turbineMessage.awaitItem())

            // Set message
            viewModel.updateCategory(brand)
            assertEquals("Success message", turbineMessage.awaitItem())

            // Reset message
            viewModel.restartMessage()
            assertEquals(null, turbineMessage.awaitItem())

            turbineMessage.cancel()
        }
    }

    @Test
    fun `observeCategory should handle empty flow`() = runTest {
        // Given
        val brandId = 1L
        coEvery { repository.getCategoryById(brandId) } returns flow { /* Empty flow */ }

        turbineScope {
            val turbineBrand = viewModel.category.testIn(backgroundScope)

            viewModel.observeCategory(brandId)

            // Should remain null since flow is empty
            assertEquals(null, turbineBrand.awaitItem())
            turbineBrand.cancel()
        }
    }

}