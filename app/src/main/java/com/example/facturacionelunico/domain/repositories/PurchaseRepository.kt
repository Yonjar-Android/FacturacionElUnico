package com.example.facturacionelunico.domain.repositories

import androidx.paging.PagingData
import com.example.facturacionelunico.domain.models.purchase.PurchaseDetailDomainModel
import com.example.facturacionelunico.domain.models.purchase.PurchaseDomainModel
import kotlinx.coroutines.flow.Flow

interface PurchaseRepository {
    fun getPurchases(): Flow<PagingData<PurchaseDomainModel>>

    fun getPurchasesWithDebt()

    suspend fun createPurchase(
        purchase: PurchaseDomainModel,
        details: List<PurchaseDetailDomainModel>,
        moneyPaid: Double
    ): String

    suspend fun createPurchaseDetail(): String
}