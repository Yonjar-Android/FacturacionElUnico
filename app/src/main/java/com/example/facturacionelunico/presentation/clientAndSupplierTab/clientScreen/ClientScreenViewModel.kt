package com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.ClientDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.ClientRepository
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
class ClientScreenViewModel @Inject constructor(
    private val repository: ClientRepository
): ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    val clients: StateFlow<List<ClientDomainModel>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getClients()
            } else {
                repository.getClients()
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


    fun createClient(client: ClientDomainModel){
        viewModelScope.launch {
            if (clientValidations(client)){
                _message.value = repository.createClient(client)
            }
        }
    }

    fun updateQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun clientValidations(client: ClientDomainModel): Boolean{

        if (client.id == 0L){
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