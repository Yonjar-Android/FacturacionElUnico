package com.example.facturacionelunico.domain.repositories

import androidx.paging.PagingData
import com.example.facturacionelunico.data.database.entities.DetalleCompraEntity
import com.example.facturacionelunico.domain.models.ProductItem
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.purchase.DetailPurchaseDomainModelUI
import com.example.facturacionelunico.domain.models.purchase.PurchaseDetailDomainModel
import com.example.facturacionelunico.domain.models.purchase.PurchaseDomainModel
import kotlinx.coroutines.flow.Flow

interface PurchaseRepository {
    fun getPurchases(): Flow<PagingData<PurchaseDomainModel>>

    fun getPurchasesWithDebt(): Flow<PagingData<PurchaseDomainModel>>

    suspend fun getPurchaseDetailById(purchaseId: Long): Flow<ResultPattern<DetailPurchaseDomainModelUI>>

    suspend fun createPurchase(
        purchase: PurchaseDomainModel,
        details: List<PurchaseDetailDomainModel>,
        moneyPaid: Double
    ): String

    suspend fun createPurchaseDetail(purchaseId: Long, products: List<ProductItem>): String

    suspend fun payPurchase(purchaseId: Long, amount: Double): String

    suspend fun updatePurchaseDetail(purchaseDetail: DetalleCompraEntity, newTotal: Double): String

    suspend fun deletePurchaseDetail(
        purchaseDetail: DetalleCompraEntity,
        newTotal: Double
    ): String
}