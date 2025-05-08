package com.example.facturacionelunico.presentation.sellScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.ClientDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.invoice.DetailInvoiceDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import com.example.facturacionelunico.domain.repositories.ClientRepository
import com.example.facturacionelunico.domain.repositories.InvoiceRepository
import com.example.facturacionelunico.domain.repositories.ProductRepository
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
class SellScreenViewModel @Inject constructor(
    private val repository: InvoiceRepository,
    private val clientRepository: ClientRepository,
    private val productRepository: ProductRepository
): ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun createInvoice(invoice: InvoiceDomainModel,
                      details: List<DetailInvoiceDomainModel>,
                      moneyPaid:Double){
        viewModelScope.launch {
            println(invoice)
            println(details)
           _message.value = repository.createInvoice(invoice,details,moneyPaid)
        }
    }

    private val _searchQueryProduct = MutableStateFlow("")
    val searchQueryProduct: StateFlow<String> = _searchQueryProduct.asStateFlow()

    fun updateQueryProduct(newQuery: String) {
        _searchQueryProduct.value = newQuery
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val products: StateFlow<List<DetailedProductModel>> = _searchQueryProduct
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                productRepository.getProducts()
            } else {
                productRepository.getProductBySearch(query)
            }
        }
        .map { result ->
            when (result) {
                is ResultPattern.Success -> {
                    _message.value = null
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

    private val _searchQueryClient = MutableStateFlow("")
    val searchQueryClient: StateFlow<String> = _searchQueryClient.asStateFlow()

    fun updateQueryClient(newQuery: String) {
        _searchQueryClient.value = newQuery
    }

    val clients: StateFlow<List<ClientDomainModel>> = _searchQueryClient
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                clientRepository.getClients()
            } else {
                clientRepository.getClientBySearch(query)
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

    fun restartMessage(){
        _message.value = null
    }
}