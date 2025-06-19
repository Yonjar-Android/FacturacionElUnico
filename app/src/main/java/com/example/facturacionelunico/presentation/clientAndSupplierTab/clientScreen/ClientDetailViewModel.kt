package com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.facturacionelunico.domain.models.client.ClientDomainModel
import com.example.facturacionelunico.domain.models.client.DetailedClientDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import com.example.facturacionelunico.domain.repositories.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ClientDetailViewModel @Inject constructor(
private val repository: ClientRepository
): ViewModel() {

    private val _client = MutableStateFlow<DetailedClientDomainModel?>(null)
    val client = _client.asStateFlow()

    private val _clientId = MutableStateFlow<Long?>(null)

    val invoices: Flow<PagingData<InvoiceDomainModel>> = _clientId
        .filterNotNull()
        .flatMapLatest { id ->
            repository.getInvoicesByClientId(id)
        }
        .cachedIn(viewModelScope)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun getClientById(id: Long){
        viewModelScope.launch {
            repository.getClientById(id).collect{ clientEntity ->
                _client.value = clientEntity
                _clientId.value = clientEntity.id
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