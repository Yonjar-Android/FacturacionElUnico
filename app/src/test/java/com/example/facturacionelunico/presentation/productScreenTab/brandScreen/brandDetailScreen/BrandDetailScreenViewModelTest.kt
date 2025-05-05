package com.example.facturacionelunico.presentation.productScreenTab.brandScreen.brandDetailScreen

import app.cash.turbine.turbineScope
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.repositories.BrandRepository
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
class BrandDetailScreenViewModelTest {

    @MockK
    lateinit var repository: BrandRepository

    lateinit var viewModel: BrandDetailScreenViewModel

    val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = BrandDetailScreenViewModel(repository)
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }

    @Test
    fun `updateBrand should update  message state`() = runTest{
        //Given
        val brandDomainModel = BrandDomainModel(1, "Test Brand")
        coEvery { repository.updateBrand(brandDomainModel) } returns "Success message"

        turbineScope { val turbineMessage = viewModel.message.testIn(backgroundScope)

            assertEquals(null, turbineMessage.awaitItem())

            //When
            viewModel.updateBrand(brandDomainModel)

            assertEquals("Success message", turbineMessage.awaitItem())

            //Then

            turbineMessage.cancel()
        }

        coVerify(exactly = 1) { repository.updateBrand(brandDomainModel) }

    }

    @Test
    fun `observeBrand should update brand state`() = runTest {
        // Given
        val brandId = 1L
        val mockBrand = BrandDomainModel(brandId, "Test Brand")
        coEvery { repository.getBrandById(brandId) } returns flow { emit(mockBrand) }

        turbineScope {
            val turbineBrand = viewModel.brand.testIn(backgroundScope)

            assertEquals(null, turbineBrand.awaitItem())
            // When
            viewModel.observeBrand(brandId)

            assertEquals(mockBrand, turbineBrand.awaitItem())
            turbineBrand.cancel()
        }

        coVerify(exactly = 1) { repository.getBrandById(brandId) }
    }

    @Test
    fun `getProductsByBrand should update products state`() = runTest {
        //Given
        val brandId = 1L
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

        coEvery { repository.getProductsByBrand(brandId) } returns productDetailed

        turbineScope {
            val turbineProducts = viewModel.products.testIn(backgroundScope)

            assertEquals(emptyList<DetailedProductModel>(), turbineProducts.awaitItem())

            viewModel.getProductsByBrand(brandId)

            val result = turbineProducts.awaitItem()
            assertEquals(productDetailed, result)
            assertEquals(result.first().brandId, brandId)
            turbineProducts.cancel()
        }

        coVerify(exactly = 1) { repository.getProductsByBrand(brandId) }
    }

    @Test
    fun `restartMessage should reset message to null`() = runTest {
        // Given
        val brand = BrandDomainModel(1, "Test Brand")
        coEvery { repository.updateBrand(brand) } returns "Success message"

        turbineScope {
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial state
            assertEquals(null, turbineMessage.awaitItem())

            // Set message
            viewModel.updateBrand(brand)
            assertEquals("Success message", turbineMessage.awaitItem())

            // Reset message
            viewModel.restartMessage()
            assertEquals(null, turbineMessage.awaitItem())

            turbineMessage.cancel()
        }
    }

    @Test
    fun `observeBrand should handle empty flow`() = runTest {
        // Given
        val brandId = 1L
        coEvery { repository.getBrandById(brandId) } returns flow { /* Empty flow */ }

        turbineScope {
            val turbineBrand = viewModel.brand.testIn(backgroundScope)

            viewModel.observeBrand(brandId)

            // Should remain null since flow is empty
            assertEquals(null, turbineBrand.awaitItem())
            turbineBrand.cancel()
        }
    }

}