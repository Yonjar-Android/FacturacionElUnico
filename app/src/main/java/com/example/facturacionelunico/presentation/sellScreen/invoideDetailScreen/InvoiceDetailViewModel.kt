package com.example.facturacionelunico.presentation.sellScreen.invoideDetailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.invoice.InvoiceDetailDomainModel
import com.example.facturacionelunico.domain.repositories.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceDetailViewModel @Inject constructor(
    private val repository: InvoiceRepository
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

    fun payInvoice(invoiceId: Long, amount: Double) {
        viewModelScope.launch {
            _message.value =repository.payInvoice(invoiceId, amount)
        }
    }

    fun restartMessage(){
        _message.value = null
    }

}