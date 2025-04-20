package com.example.facturacionelunico.presentation.productScreenTab.productScreen.productDetailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel() {

    private val _product = MutableStateFlow<DetailedProductModel?>(null)
    val product: StateFlow<DetailedProductModel?> = _product.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Función para la carga del producto
    fun loadProduct(id: Long) {
        viewModelScope.launch {
            when(val response = repository.getProductById(id)){
                is ResultPattern.Error -> {
                    _message.value = response.message
                }
                is ResultPattern.Success -> {
                    _product.value = response.data
                }
            }
        }
    }

    fun restartMessage(){
        _message.value = null
    }

}