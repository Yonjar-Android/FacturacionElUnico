package com.example.facturacionelunico.presentation.productScreenTab.categoryScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.CategoryRepository
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

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CategoryScreenViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun updateQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    init {
        _message.value = "Hola papa"
    }

    val categories: StateFlow<List<CategoryDomainModel>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getCategories()
            } else {
                repository.getCategoryByName(query)
            }
        }.map { result ->
            when (result) {
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

    fun createCategory(name: String) {
        viewModelScope.launch {
            val response = repository.createCategory(name)
            _message.value = response
        }
    }

    fun restartMessage() {
        _message.value = null
    }
}