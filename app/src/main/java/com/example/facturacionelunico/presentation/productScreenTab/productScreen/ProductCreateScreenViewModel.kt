package com.example.facturacionelunico.presentation.productScreenTab.productScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.ProductDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductCreateScreenViewModel @Inject constructor(
    private val repository: ProductRepository,
    brandRepository: BrandRepository,
    categoryRepository: CategoryRepository
): ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _searchQueryCategory = MutableStateFlow("")
    val searchQueryCategory: StateFlow<String> = _searchQueryCategory.asStateFlow()

    fun updateQueryCategory(newQuery: String) {
        _searchQueryCategory.value = newQuery
    }

    val categories: StateFlow<List<CategoryDomainModel>> = _searchQueryCategory
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                categoryRepository.getCategories()
            } else {
                categoryRepository.getCategoryByName(query)
            }
        }
        .map { result ->
            when (result) {
                is ResultPattern.Success -> {
                    _message.value = null
                    result.data
                }
                is ResultPattern.Error -> {
                    _message.value = result.message ?: "Ha ocurrido un error desconocido"
                    emptyList()
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQueryBrand = MutableStateFlow("")
    val searchQueryBrand : StateFlow<String> = _searchQueryBrand.asStateFlow()

    fun updateQueryBrand(newQuery: String) {
        _searchQueryBrand.value = newQuery
    }

    // Con flatMapLatest, cada vez que cambia el query se ejecuta la consulta correspondiente.
    val brands: StateFlow<List<BrandDomainModel>> = _searchQueryBrand
        .debounce(300) // Para evitar llamadas excesivas mientras se escribe.
        .flatMapLatest { query ->
            // Si el query está vacío, podrías mostrar todas las marcas, o bien una lista vacía según la necesidad.
            if (query.isBlank()) {
                brandRepository.getBrands()
            } else {
                brandRepository.getBrandByName(query)
            }
        }
        .map { result ->
            when(result){
                is ResultPattern.Success -> {
                    restartMessage()
                    result.data
                }
                is ResultPattern.Error -> {
                    _message.value = result.message ?: "Ha ocurrido un error desconocido"
                    emptyList()
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createProduct(product: ProductDomainModel){
        viewModelScope.launch {
            val response = repository.createProduct(product)
            _message.value = response
        }
    }

    fun restartMessage(){
        _message.value = null
    }

}