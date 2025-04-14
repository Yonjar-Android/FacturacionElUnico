package com.example.facturacionelunico.presentation.productScreenTab.brandScreen.brandDetailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.repositories.BrandRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandDetailScreenViewModel @Inject constructor(
    private val repository: BrandRepository
) : ViewModel() {

    private var _products = MutableStateFlow<List<DetailedProductModel>>(emptyList())
    val products = _products.asStateFlow()

    private val _brand = MutableStateFlow<BrandDomainModel?>(null)
    val brand = _brand.asStateFlow()

    fun observeBrand(brandId: Long) {
        viewModelScope.launch {
            repository.getBrandById(brandId).collect { brandEntity ->
                _brand.value = brandEntity
            }
        }
    }

    fun getProductsByBrand(brandId: Long) {
        viewModelScope.launch {
            val response = repository.getProductsByBrand(brandId)
            _products.value = response
        }
    }

    fun updateBrand(brand: BrandDomainModel) {
        viewModelScope.launch {
            repository.updateBrand(brand)
        }
    }

}