package com.example.facturacionelunico.presentation.productScreenTab.productScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ResultPattern
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
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductScreenViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun updateQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // Flow necesario para la búsqueda inmediata de productos
    // para que carguen justo al momento que el usuario escribe en el textField
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val products: StateFlow<PagingData<DetailedProductModel>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getProducts()
            } else {
                repository.getProductBySearch(query)
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

    fun restartMessage(){
        _message.value = null
    }

}