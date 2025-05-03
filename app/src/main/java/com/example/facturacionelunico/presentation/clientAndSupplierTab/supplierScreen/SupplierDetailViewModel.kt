package com.example.facturacionelunico.presentation.clientAndSupplierTab.supplierScreen

import androidx.lifecycle.ViewModel
import com.example.facturacionelunico.domain.repositories.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SupplierDetailViewModel @Inject constructor(
    private val repository: SupplierRepository
): ViewModel() {

}