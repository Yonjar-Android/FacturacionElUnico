package com.example.facturacionelunico.presentation.productScreenTab.productScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.ProductDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.BrandRepository
import com.example.facturacionelunico.domain.repositories.CategoryRepository
import com.example.facturacionelunico.domain.repositories.ProductRepository
import com.example.facturacionelunico.utils.validations.ValidationFunctions
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

    private val _back = MutableStateFlow<Boolean>(false)
    val back: StateFlow<Boolean> = _back

    fun updateQueryCategory(newQuery: String) {
        _searchQueryCategory.value = newQuery
    }

    val categories: StateFlow<PagingData<CategoryDomainModel>> = _searchQueryCategory
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
                    PagingData.empty()
                }
            }
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PagingData.empty()
        )

    private val _searchQueryBrand = MutableStateFlow("")
    val searchQueryBrand : StateFlow<String> = _searchQueryBrand.asStateFlow()

    fun updateQueryBrand(newQuery: String) {
        _searchQueryBrand.value = newQuery
    }

    // Con flatMapLatest, cada vez que cambia el query se ejecuta la consulta correspondiente.
    val brands: StateFlow<PagingData<BrandDomainModel>> = _searchQueryBrand
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
                    PagingData.empty()
                }
            }
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PagingData.empty()
        )

    fun createProduct(product: ProductDomainModel){
        viewModelScope.launch {
            if(validations(product)){
                val response = repository.createProduct(product)
                _message.value = response

                if (!response.contains("Error")){
                    _back.value = true
                }
            }
        }
    }

    // Validaciones para crear producto
    private fun validations(product: ProductDomainModel): Boolean {
        if (product.name.isEmpty()){
            _message.value = "El campo nombre no puede estar vacío"
            return false
        }
        if (product.stock.toString().isEmpty()){
            _message.value = "El campo stock no puede estar vacío"
            return false
        }
        if (!ValidationFunctions.isValidInt(product.stock.toString()) || product.stock.toInt() <= 0){
            _message.value = "El valor ingresado en stock no es válido"
            return false
        }
        if (!ValidationFunctions.isValidDouble(product.priceSell.toString()) || product.priceSell.toDouble() <= 0.0){
            _message.value = "El valor ingresado en precio venta no es válido"
            return false
        }
        if (!ValidationFunctions.isValidDouble(product.priceBuy.toString()) || product.priceBuy.toDouble() <= 0.0){
            _message.value = "El valor ingresado en precio compra no es válido"
            return false
        }
        return true
    }

    fun restartMessage(){
        _message.value = null
    }

}