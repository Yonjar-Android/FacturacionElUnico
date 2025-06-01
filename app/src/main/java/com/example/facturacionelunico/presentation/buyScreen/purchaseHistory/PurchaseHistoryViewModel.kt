package com.example.facturacionelunico.presentation.buyScreen.purchaseHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import com.example.facturacionelunico.domain.models.purchase.PurchaseDomainModel
import com.example.facturacionelunico.domain.repositories.InvoiceRepository
import com.example.facturacionelunico.domain.repositories.PurchaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PurchaseHistoryViewModel @Inject constructor(
    private val repository: PurchaseRepository
): ViewModel() {
    private val selectedOption = MutableStateFlow("Todas")

    val purchases: StateFlow<PagingData<PurchaseDomainModel>> = selectedOption
        .flatMapLatest { option ->
            if (option == "Pendientes") {
                repository.getPurchasesWithDebt()
            } else {
                repository.getPurchases()
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