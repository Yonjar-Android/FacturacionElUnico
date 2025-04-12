package com.example.facturacionelunico.presentation.productScreenTab.productScreen.productDetailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.repositories.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel() {

    private val _product = MutableStateFlow<DetailedProductModel?>(null)
    val product: StateFlow<DetailedProductModel?> = _product.asStateFlow()

    fun loadProduct(id: Long) {
        viewModelScope.launch {
            _product.value = repository.getProductById(id)
        }
    }

}