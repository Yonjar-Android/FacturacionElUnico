package com.example.facturacionelunico.presentation.sellScreen.invoiceScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.domain.models.client.DetailedClientDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import com.example.facturacionelunico.domain.repositories.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceScreenViewModel @Inject constructor(
    private val repository: InvoiceRepository
): ViewModel() {
    private val _invoices = MutableStateFlow<List<InvoiceDomainModel>>(emptyList())
    val invoices = _invoices.asStateFlow()

    init {
        viewModelScope.launch {
            _invoices.value = repository.getInvoices()
        }
    }

    fun getInvoicesBySelectedOption(option: String){
        viewModelScope.launch {
            if (option == "Pendientes"){
                _invoices.value = repository.getInvoicesWithDebt()
            } else{
                _invoices.value = repository.getInvoices()
            }
        }
    }
}