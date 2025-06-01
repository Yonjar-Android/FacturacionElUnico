package com.example.facturacionelunico.presentation.clientAndSupplierTab.supplierScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.supplier.DetailedSupplierDomainModel
import com.example.facturacionelunico.domain.models.supplier.SupplierDomainModel
import com.example.facturacionelunico.domain.repositories.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupplierDetailViewModel @Inject constructor(
    private val repository: SupplierRepository
): ViewModel() {

    private val _supplier = MutableStateFlow<DetailedSupplierDomainModel?>(null)
    val supplier  = _supplier.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message


    fun getSupplierById(id: Long){
        viewModelScope.launch {
            repository.getSupplierById(id).collect{ supplierEntity ->
                _supplier.value = supplierEntity
            }
        }
    }

    fun updateSupplier(supplier: SupplierDomainModel){
        viewModelScope.launch {
            if (clientValidations(supplier)){
                _message.value = repository.updateSupplier(supplier)
            }
        }
    }

    // Validaciones antes de enviar los datos al repositorio
    fun clientValidations(supplier: SupplierDomainModel): Boolean{

        if (supplier.company.isEmpty()){
            _message.value = "Error: Rellene el campo Nombre de Empresa"
            return false
        }

        if (supplier.contactName.isEmpty()){
            _message.value = "Error: Rellene el campo Nombre de contacto"
            return false
        }

        if (supplier.email?.isNotEmpty() == true) {
            if (!isValidEmail(supplier.email)) {
                _message.value = "Error: El email no es valido"
                return false
            }
        }

        return true
    }

    // Función para verificar si un email es válido
    private fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    fun restartMessage(){
        _message.value = null
    }
}