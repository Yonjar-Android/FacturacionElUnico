package com.example.facturacionelunico.presentation.productScreenTab.categoryScreen.categoryDetailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.repositories.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryDetailScreenViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private var _products = MutableStateFlow<List<DetailedProductModel>>(emptyList())
    val products = _products.asStateFlow()

    private val _category = MutableStateFlow<CategoryDomainModel?>(null)
    val category = _category.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun observeCategory(categoryId: Long) {
        viewModelScope.launch {
            repository.getCategoryById(categoryId).collect { categoryEntity ->
                _category.value = categoryEntity
            }
        }
    }

    fun getProductsByCategory(categoryId: Long) {
        viewModelScope.launch {
            val response = repository.getProductsByCategory(categoryId)
            _products.value = response
        }
    }

    fun updateCategory(categoryDomainModel: CategoryDomainModel) {
        viewModelScope.launch {
            val response = repository.updateCategory(categoryDomainModel)
            _message.value = response

        }
    }

    fun restartMessage(){
        _message.value = null
    }
}