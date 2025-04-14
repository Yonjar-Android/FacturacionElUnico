package com.example.facturacionelunico.presentation.productScreenTab.brandScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.repositories.BrandRepository
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

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class BrandScreenViewModel @Inject constructor(
    private val repository: BrandRepository
): ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // Con flatMapLatest, cada vez que cambia el query se ejecuta la consulta correspondiente.
    val brands: StateFlow<List<BrandDomainModel>> = _searchQuery
        .debounce(300) // Para evitar llamadas excesivas mientras se escribe.
        .flatMapLatest { query ->
            // Si el query está vacío, podrías mostrar todas las marcas, o bien una lista vacía según la necesidad.
            if (query.isBlank()) {
                repository.getBrands()
            } else {
                repository.getBrandByName(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createBrand(name: String){
        viewModelScope.launch {
            repository.createBrand(name)
        }
    }

}