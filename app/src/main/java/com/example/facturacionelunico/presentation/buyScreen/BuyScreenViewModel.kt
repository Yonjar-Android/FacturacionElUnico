package com.example.facturacionelunico.presentation.buyScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.SupplierDomainModel
import com.example.facturacionelunico.domain.models.client.DetailedClientLocalModel
import com.example.facturacionelunico.domain.models.purchase.PurchaseDetailDomainModel
import com.example.facturacionelunico.domain.models.purchase.PurchaseDomainModel
import com.example.facturacionelunico.domain.repositories.ProductRepository
import com.example.facturacionelunico.domain.repositories.PurchaseRepository
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
class BuyScreenViewModel @Inject constructor(
    private val repository: PurchaseRepository,
    private val productRepository: ProductRepository,
    private val supplierRepository: SupplierRepository
): ViewModel() {
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun createPurchase(
        purchase: PurchaseDomainModel,
        details: List<PurchaseDetailDomainModel>,
        moneyPaid: Double
    ){
        viewModelScope.launch {
            _message.value = repository.createPurchase(purchase, details, moneyPaid)
        }
    }

    private val _searchQueryProduct = MutableStateFlow("")
    val searchQueryProduct: StateFlow<String> = _searchQueryProduct.asStateFlow()

    fun updateQueryProduct(newQuery: String) {
        _searchQueryProduct.value = newQuery
    }

    val products: StateFlow<PagingData<DetailedProductModel>> = _searchQueryProduct
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
                    result.data
                }

                is ResultPattern.Error -> {
                    _message.value = result.message ?: "Error: Ha ocurrido un error desconocido"
                    PagingData.empty()
                }
            }
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PagingData.empty()
        )

    private val _searchQuerySupplier = MutableStateFlow("")
    val searchQuerySupplier: StateFlow<String> = _searchQuerySupplier.asStateFlow()

    fun updateQuerySupplier(newQuery: String) {
        _searchQuerySupplier.value = newQuery
    }

    val clients: StateFlow<PagingData<SupplierDomainModel>> = _searchQuerySupplier
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                supplierRepository.getSuppliers()
            } else {
                supplierRepository.getSuppliersBySearch(query)
            }
        }.map { result ->
            when (result) {
                is ResultPattern.Success -> {
                    result.data
                }

                is ResultPattern.Error -> {
                    _message.value = result.message ?: "Error: Ha ocurrido un error desconocido"
                    PagingData.empty()
                }
            }
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PagingData.empty()
        )

    fun restartMessage() {
        _message.value = null
    }
}