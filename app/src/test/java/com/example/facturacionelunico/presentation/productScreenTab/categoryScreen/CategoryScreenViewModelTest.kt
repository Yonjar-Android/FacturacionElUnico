package com.example.facturacionelunico.presentation.productScreenTab.categoryScreen

import app.cash.turbine.turbineScope
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.CategoryRepository
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
class CategoryScreenViewModelTest {

    @MockK
    lateinit var repository: CategoryRepository

    lateinit var viewModel: CategoryScreenViewModel

    val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = CategoryScreenViewModel(repository)
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
    fun `createCategory should update message`() = runTest {
        // Given
        val category = "Llantas"
        coEvery { repository.createCategory(category) } returns "Categoría creada exitosamente"

        turbineScope {
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial State
            assertTrue(turbineMessage.awaitItem() == null)


            // Call the function to create the category
            viewModel.createCategory(category)

            // Second State
            assertTrue(turbineMessage.awaitItem() == "Categoría creada exitosamente")

            turbineMessage.cancel()
        }

        coVerify(exactly = 1) { repository.createCategory(category) }
    }

    @Test
    fun `getCategories should return all categories when query is empty`() = runTest {
        // Given
        val mockCategories = listOf(CategoryDomainModel(1, "Category1"), CategoryDomainModel(2, "Category2"))
        coEvery { repository.getCategories() } returns flow { emit(ResultPattern.Success(mockCategories)) }

        turbineScope {
            val turbineCategories = viewModel.categories.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<CategoryDomainModel>(), turbineCategories.awaitItem())

            // Update query to empty string
            viewModel.updateQuery("")
            testDispatcher.scheduler.advanceUntilIdle()

            // Should return all categories
            assertEquals(mockCategories, turbineCategories.awaitItem())

            turbineCategories.cancel()
        }
        coVerify(exactly = 1) { repository.getCategories() }
    }

    @Test
    fun `getCategories should return filtered categories when query is not empty`() = runTest {
        // Given
        val query = "Category1"
        val mockCategories = listOf(CategoryDomainModel(1, "Category1"))
        coEvery { repository.getCategoryByName(query) } returns flow { emit(ResultPattern.Success(mockCategories)) }

        turbineScope {
            val turbineCategories = viewModel.categories.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<CategoryDomainModel>(), turbineCategories.awaitItem())

            // Update query
            viewModel.updateQuery(query)
            testDispatcher.scheduler.advanceUntilIdle() // Wait for debounce

            // Should return filtered categories
            val result = turbineCategories.awaitItem()
            assertEquals(mockCategories, result)
            assertTrue(result.first().categoryName.contains(query))

            turbineCategories.cancel()
        }

        coVerify(exactly = 1) { repository.getCategoryByName(query) }
    }

    @Test
    fun `categories should handle error and update message`() = runTest {
        // Given
        val query = "Invalid"
        val errorMessage = "Error fetching categories"
        coEvery { repository.getCategoryByName(query) } returns flow { emit(ResultPattern.Error(message = errorMessage)) }

        turbineScope {
            val turbineCategories= viewModel.categories.testIn(backgroundScope)
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial states
            assertEquals(emptyList<CategoryDomainModel>(), turbineCategories.awaitItem())
            assertEquals(null, turbineMessage.awaitItem())

            // Update query
            viewModel.updateQuery(query)
            testDispatcher.scheduler.advanceUntilIdle()

            // Should return empty list and update message
            assertEquals(errorMessage, turbineMessage.awaitItem())

            turbineCategories.cancel()
            turbineMessage.cancel()
        }
        coVerify(exactly = 1) { repository.getCategoryByName(query) }
    }

    @Test
    fun `restartMessage should reset message to null`() = runTest {
        // Given
        val category = "Test Category"
        coEvery { repository.createCategory(category) } returns "Success message"

        turbineScope {
            val turbineMessage = viewModel.message.testIn(backgroundScope)

            // Initial state
            assertEquals(null, turbineMessage.awaitItem())

            // Create category to set message
            viewModel.createCategory(category)
            assertEquals("Success message", turbineMessage.awaitItem())

            // Restart message
            viewModel.restartMessage()
            assertEquals(null, turbineMessage.awaitItem())

            turbineMessage.cancel()
        }
        coVerify(exactly = 1) { repository.createCategory(category) }
    }

    @Test
    fun `categories should debounce queries`() = runTest(testDispatcher) {
        // Given
        val mockCategories = listOf(CategoryDomainModel(1, "Category"))
        coEvery { repository.getCategoryByName("Category") } returns flow { emit(ResultPattern.Success(mockCategories)) }

        turbineScope {
            val turbineCategories = viewModel.categories.testIn(backgroundScope)

            // Initial empty list
            assertEquals(emptyList<CategoryDomainModel>(), turbineCategories.awaitItem())

            // Rapid successive queries
            viewModel.updateQuery("C")
            viewModel.updateQuery("Ca")
            // ... más updates
            viewModel.updateQuery("Category")

            // Avanzar menos que el debounce
            testDispatcher.scheduler.advanceTimeBy(200)
            turbineCategories.expectNoEvents()

            // Avanzar pasado el debounce
            testDispatcher.scheduler.advanceTimeBy(200)
            assertEquals(mockCategories, turbineCategories.awaitItem())

            turbineCategories.cancel()
        }
    }

}