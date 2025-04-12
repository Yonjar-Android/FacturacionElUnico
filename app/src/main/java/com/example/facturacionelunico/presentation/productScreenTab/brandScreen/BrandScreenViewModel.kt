package com.example.facturacionelunico.presentation.productScreenTab.brandScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.repositories.BrandRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandScreenViewModel @Inject constructor(
    private val repository: BrandRepository
): ViewModel() {

    val brands: StateFlow<List<BrandDomainModel>> = repository.getBrands()
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