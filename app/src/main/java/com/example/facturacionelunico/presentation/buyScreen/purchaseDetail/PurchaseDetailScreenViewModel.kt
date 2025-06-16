package com.example.facturacionelunico.presentation.buyScreen.purchaseDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.facturacionelunico.data.database.entities.DetalleCompraEntity
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ProductItem
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.purchase.DetailPurchaseDomainModelUI
import com.example.facturacionelunico.domain.repositories.ProductRepository
import com.example.facturacionelunico.domain.repositories.PurchaseRepository
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

@HiltViewModel
class PurchaseDetailScreenViewModel @Inject constructor(
    private val repository: PurchaseRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _purchase = MutableStateFlow<DetailPurchaseDomainModelUI?>(null)
    val purchase = _purchase.asStateFlow()

    fun getInvoiceDetail(invoiceId: Long) {
        viewModelScope.launch {
            repository.getPurchaseDetailById(invoiceId).collect { result ->
                when (result) {
                    is ResultPattern.Success -> {
                        _purchase.value = result.data
                    }

                    is ResultPattern.Error -> {
                        _message.value = result.message
                    }
                }
            }
        }
    }

    fun payInvoice(purchaseId: Long, amount: Double) {
        viewModelScope.launch {
            _message.value = repository.payPurchase(purchaseId, amount)
        }
    }

    fun addProductsToPurchase(products: List<ProductItem>) {
        viewModelScope.launch {
            _message.value = repository.createPurchaseDetail(
                purchaseId = purchase.value?.id ?: 0,
                products = products
            )
        }
    }

    suspend fun updateProduct(product: ProductItem, newTotal: Double):String {

            val purchaseDetailUpdated = DetalleCompraEntity(
                id = product.detailId,
                idCompra = purchase.value?.id ?: 0,
                idProducto = product.id,
                cantidad = product.quantity,
                precio = product.price,
                subtotal = product.subtotal
            )

        val message = repository.updatePurchaseDetail(purchaseDetailUpdated, newTotal)
        if (message == "Error: El nuevo total es menor a la cantidad ya abonada") {
            return message
        }

        _message.value = message
        return ""

    }

    suspend fun deleteProduct(product: ProductItem, newTotal: Double):String {
        val purchaseDetailUpdated = DetalleCompraEntity(
            id = product.detailId,
            idCompra = purchase.value?.id ?: 0,
            idProducto = product.id,
            cantidad = product.quantity,
            precio = product.price,
            subtotal = product.subtotal
        )

        val message = repository.deletePurchaseDetail(purchaseDetailUpdated, newTotal)
        if (message == "Error: El nuevo total es menor a la cantidad ya abonada") {
            return message
        }

        _message.value = message
        return ""
    }

    private val _searchQueryProduct = MutableStateFlow("")
    val searchQueryProduct: StateFlow<String> = _searchQueryProduct.asStateFlow()

    fun updateQueryProduct(newQuery: String) {
        _searchQueryProduct.value = newQuery
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
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
                    _message.value = result.message ?: "Ha ocurrido un error desconocido"
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