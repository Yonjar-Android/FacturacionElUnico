package com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.client.ClientDomainModel
import com.example.facturacionelunico.domain.models.client.DetailedClientDomainModel
import com.example.facturacionelunico.domain.repositories.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientDetailViewModel @Inject constructor(
private val repository: ClientRepository
): ViewModel() {

    private val _client = MutableStateFlow<DetailedClientDomainModel?>(null)
    val client = _client.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun getClientById(id: Long){
        viewModelScope.launch {
            repository.getClientById(id).collect{ clientEntity ->
                _client.value = clientEntity
            }
        }
    }

    fun updateClient(client: ClientDomainModel){
        viewModelScope.launch {
            if (clientValidations(client)){
             _message.value = repository.updateClient(client)
            }
        }
    }

    // Validaciones antes de enviar los datos al repositorio
    fun clientValidations(client: ClientDomainModel): Boolean{

        if (client.numberIdentifier == 0){
            _message.value = "Error: Rellene el campo número de cliente"
            return false
        }

        if (client.name.isEmpty()){
            _message.value = "Error: Rellene el campo nombre"
            return false
        }

        if (client.lastName.isEmpty()){
            _message.value = "Error: Rellene el campo apellido"
            return false
        }

        return true
    }

    fun restartMessage(){
        _message.value = null
    }

}