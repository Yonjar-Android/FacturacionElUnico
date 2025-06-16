package com.example.facturacionelunico.presentation.sellScreen.invoideDetailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.facturacionelunico.data.database.entities.DetalleVentaEntity
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ProductItem
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.invoice.InvoiceDetailDomainModel
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

@HiltViewModel
class InvoiceDetailViewModel @Inject constructor(
    private val repository: InvoiceRepository,
    private val productRepository: ProductRepository
): ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _invoice = MutableStateFlow<InvoiceDetailDomainModel?>(null)
    val invoice = _invoice.asStateFlow()

    fun getInvoiceDetail(invoiceId: Long) {
        viewModelScope.launch {
            repository.getInvoiceDetailById(invoiceId).collect { result ->
                when (result) {
                    is ResultPattern.Success -> {
                        _invoice.value = result.data
                    }
                    is ResultPattern.Error -> {
                    _message.value = result.message
                    }
                }
            }
        }
    }

    // Función para actualizar un producto de la factura
    fun updateProduct(product: ProductItem, newTotal:Double){
        viewModelScope.launch {

            val newDetail = DetalleVentaEntity(
                id = product.detailId,
                idVenta = invoice.value?.invoiceId ?: 0,
                idProducto = product.id,
                cantidad = product.quantity,
                precio = product.price,
                subtotal = product.subtotal,
                fechaActualizacion = System.currentTimeMillis(),
                precioCompra = product.purchasePrice
            )
            _message.value = repository.updateInvoiceDetail(newDetail, newTotal)
        }
    }

    // Función para eliminar un producto de la factura
    fun deleteProduct(product: ProductItem, newTotal:Double){
        viewModelScope.launch {
            val detail = DetalleVentaEntity(
                id = product.detailId,
                idVenta = invoice.value?.invoiceId ?: 0,
                idProducto = product.id,
                cantidad = product.quantity,
                precio = product.price,
                subtotal = product.subtotal,
                fechaActualizacion = System.currentTimeMillis(),
                precioCompra = product.purchasePrice
            )
            _message.value = repository.deleteInvoiceDetail(detail, newTotal)
        }
    }

    fun payInvoice(invoiceId: Long, amount: Double) {
        viewModelScope.launch {
            _message.value =repository.payInvoice(invoiceId, amount)
        }
    }

    fun addProductsToInvoice(products: List<ProductItem>){
        viewModelScope.launch {
            _message.value = repository.createInvoiceDetail(
                invoiceId = invoice.value?.invoiceId ?: 0,
                products = products
            )
        }
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

    fun restartMessage(){
        _message.value = null
    }

}