package com.example.facturacionelunico.presentation.productScreenTab.productScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ProductDomainModel
import com.example.facturacionelunico.domain.repositories.BrandRepository
import com.example.facturacionelunico.domain.repositories.CategoryRepository
import com.example.facturacionelunico.domain.repositories.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductScreenViewModel @Inject constructor(
    private val repository: ProductRepository,
    brandRepository: BrandRepository,
    categoryRepository: CategoryRepository
): ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val products: StateFlow<List<DetailedProductModel>> = _searchQuery
        .debounce(300) // Para evitar llamadas excesivas mientras se escribe, ajusta el tiempo según convenga
        .flatMapLatest { query ->
            // Si el query está vacío, podrías devolver todos los productos o tratar el caso a tu conveniencia.
            if (query.isBlank()) {
                repository.getProducts()  // Función original que trae todos los productos
            } else {
                repository.getProductBySearch(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categories: StateFlow<List<CategoryDomainModel>> = categoryRepository.getCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val brands: StateFlow<List<BrandDomainModel>> = brandRepository.getBrands()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createProduct(product: ProductDomainModel){
        viewModelScope.launch {
            repository.createProduct(
                product
            )
        }
    }

}