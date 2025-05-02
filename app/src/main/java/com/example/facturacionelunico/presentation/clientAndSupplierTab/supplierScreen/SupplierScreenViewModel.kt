package com.example.facturacionelunico.presentation.clientAndSupplierTab.supplierScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.ClientDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.SupplierDomainModel
import com.example.facturacionelunico.domain.repositories.SupplierRepository
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
class SupplierScreenViewModel @Inject constructor(
    private val repository: SupplierRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun updateQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    val suppliers: StateFlow<List<SupplierDomainModel>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getSuppliers()
            } else {
                repository.getSuppliers()
            }
        }.map { result ->
            when (result) {
                is ResultPattern.Success -> {
                    result.data
                }

                is ResultPattern.Error -> {
                    _message.value = result.message ?: "Error: Ha ocurrido un error desconocido"
                    emptyList()
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createSupplier(supplier: SupplierDomainModel) {
        viewModelScope.launch {
            if (supplierValidations(supplier)) {
                _message.value = repository.createSupplier(supplier)
            }
        }
    }

    private fun supplierValidations(supplier: SupplierDomainModel): Boolean {

        if (supplier.company.isEmpty()){
            _message.value = "Error: Rellene el campo empresa"
            return false
        }

        if (supplier.contactName.isEmpty()){
            _message.value = "Error: Rellene el campo contacto"
            return false
        }

        if (supplier.email?.isNotEmpty() == true){
            if (!isValidEmail(supplier.email)){
                _message.value = "Error: El email no es valido"
                return false
            }
        }
        return true
    }

    private fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    fun restartMessage() {
        _message.value = null
    }
}