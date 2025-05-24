package com.example.facturacionelunico.presentation.sellScreen.invoiceScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.facturacionelunico.domain.models.client.DetailedClientDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import com.example.facturacionelunico.domain.repositories.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InvoiceScreenViewModel @Inject constructor(
    private val repository: InvoiceRepository
): ViewModel() {
    private val selectedOption = MutableStateFlow("Todas")

    val invoices: StateFlow<PagingData<InvoiceDomainModel>> = selectedOption
        .flatMapLatest { option ->
            if (option == "Pendientes") {
                repository.getInvoicesWithDebt()
            } else {
                repository.getInvoices()
            }
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PagingData.empty()
        )

    fun getInvoicesBySelectedOption(option: String) {
        selectedOption.value = option
    }
}