package com.example.facturacionelunico.presentation.productScreenTab.categoryScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.repositories.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryScreenViewModel @Inject constructor(
private val repository: CategoryRepository
):ViewModel() {

    val categories: StateFlow<List<CategoryDomainModel>> = repository.getCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createCategory(name:String) {
        viewModelScope.launch {
            repository.createCategory(name)
        }
    }
}