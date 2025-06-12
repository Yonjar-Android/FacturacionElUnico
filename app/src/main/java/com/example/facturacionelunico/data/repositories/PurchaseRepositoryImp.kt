package com.example.facturacionelunico.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.facturacionelunico.data.database.AppDatabase
import com.example.facturacionelunico.data.database.dao.AbonoCompraDao
import com.example.facturacionelunico.data.database.dao.CompraDao
import com.example.facturacionelunico.data.database.dao.DetalleAbonoCompraDao
import com.example.facturacionelunico.data.database.dao.DetalleCompraDao
import com.example.facturacionelunico.data.database.entities.AbonoCompraEntity
import com.example.facturacionelunico.data.database.entities.CompraEntity
import com.example.facturacionelunico.data.database.entities.DetalleAbonoCompraEntity
import com.example.facturacionelunico.data.database.entities.DetalleCompraEntity
import com.example.facturacionelunico.domain.models.ProductItem
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.purchase.DetailPurchaseDomainModelUI
import com.example.facturacionelunico.domain.models.purchase.PurchaseDetailDomainModel
import com.example.facturacionelunico.domain.models.purchase.PurchaseDomainModel
import com.example.facturacionelunico.domain.repositories.PurchaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PurchaseRepositoryImp @Inject constructor(
    private val appDatabase: AppDatabase,
    private val purchaseDao: CompraDao,
    private val purchaseDetailDao: DetalleCompraDao,
    private val compraAbonoDao: AbonoCompraDao,
    private val detalleAbonoCompraDao: DetalleAbonoCompraDao
) : PurchaseRepository {
    override fun getPurchases(): Flow<PagingData<PurchaseDomainModel>> {
        return runCatching {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 5
                ),
                pagingSourceFactory = { purchaseDao.getAll() }
            ).flow
                .map { pagingData ->
                    pagingData.map {
                        PurchaseDomainModel(
                            purchaseId = it.id,
                            purchaseDate = it.fechaCompra,
                            total = it.total,
                            supplierId = it.idProveedor,
                            state = it.estado
                        )
                    }
                }
        }.getOrElse {
            flow { emit(PagingData.empty()) }
        }
    }

    override fun getPurchasesWithDebt(): Flow<PagingData<PurchaseDomainModel>> {
        return runCatching {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 5
                ),
                pagingSourceFactory = { purchaseDao.getPurchasesWithDebt() }
            ).flow
                .map { pagingData ->
                    pagingData.map {
                        PurchaseDomainModel(
                            purchaseId = it.id,
                            purchaseDate = it.fechaCompra,
                            total = it.total,
                            supplierId = it.idProveedor,
                            state = it.estado
                        )
                    }
                }
        }.getOrElse {
            flow { emit(PagingData.empty()) }
        }
    }

    override suspend fun getPurchaseDetailById(purchaseId: Long): Flow<ResultPattern<DetailPurchaseDomainModelUI>> {
        val purchaseFlow = purchaseDao.getPurchaseDetailById(purchaseId)
        val productsFlow = purchaseDetailDao.getDetailsByPurchaseId(purchaseId)


        return combine(purchaseFlow, productsFlow) { purchase, products ->
            runCatching {
                ResultPattern.Success(
                    DetailPurchaseDomainModelUI(
                        id = purchase.id,
                        supplier = purchase.company,
                        total = purchase.total,
                        debt = purchase.totalPendiente,
                        products = products
                    )
                )
            }.getOrElse {
                ResultPattern.Error(exception = it, message = "Error: ${it.message}")
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun createPurchase(
        purchase: PurchaseDomainModel,
        details: List<PurchaseDetailDomainModel>,
        moneyPaid: Double
    ): String {
        return runCatching {
            appDatabase.withTransaction {
                val purchaseEntity = CompraEntity(
                    fechaCompra = purchase.purchaseDate,
                    total = purchase.total,
                    idProveedor = purchase.supplierId,
                    estado = purchase.state
                )

                val idPurchase = purchaseDao.insert(purchaseEntity)
                details.forEach {
                    val detailEntity = DetalleCompraEntity(
                        idCompra = idPurchase,
                        idProducto = it.productId,
                        cantidad = it.quantity,
                        precio = it.price,
                        subtotal = it.subtotal
                    )
                    purchaseDetailDao.insert(detailEntity)
                }

                val abonoEntity = AbonoCompraEntity(
                    idCompra = idPurchase,
                    fechaCreacion = purchase.purchaseDate,
                    totalAPagar = purchase.total,
                    totalPendiente = purchase.total
                )

                val idAbono = compraAbonoDao.insert(abonoEntity)

                if (moneyPaid > 0) {
                    val abonoDetalleEntity = DetalleAbonoCompraEntity(
                        idAbonoCompra = idAbono,
                        monto = moneyPaid,
                        fechaAbono = System.currentTimeMillis()
                    )

                    detalleAbonoCompraDao.insert(abonoDetalleEntity)
                }

            }
            "Compra creada con éxito"
        }.getOrElse {
            "Error: ${it.message}"
        }
    }

    override suspend fun createPurchaseDetail(
        purchaseId: Long,
        products: List<ProductItem>
    ): String {
        return runCatching {
            appDatabase.withTransaction {
                products.forEach {
                    val detailEntity = DetalleCompraEntity(
                        idCompra = purchaseId,
                        idProducto = it.id,
                        cantidad = it.quantity,
                        precio = it.price,
                        subtotal = it.subtotal
                    )
                    purchaseDetailDao.insert(detailEntity)
                }

                val newTotal = products.sumOf { it.subtotal }
                val purchase = purchaseDao.getPurchaseById(purchaseId)

                purchaseDao.update(
                    purchase.copy(
                        total = purchase.total + newTotal,
                        estado = "PENDIENTE"
                    )
                )

                val abono = compraAbonoDao.getAbonoByPurchaseId(purchaseId)
                compraAbonoDao.update(
                    abono.copy(
                        totalPendiente = abono.totalPendiente + newTotal,
                        totalAPagar = abono.totalAPagar + newTotal
                    )
                )

                "Compra actualizada con éxito"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }

    override suspend fun payPurchase(
        purchaseId: Long,
        amount: Double
    ): String {
        return runCatching {
            val abono = compraAbonoDao.getAbonoByPurchaseId(purchaseId)

            if (abono.totalPendiente < amount) {
                "Error: Se ha intentado pagar más de lo que se debe"
            } else {
                val abonoDetalleEntity = DetalleAbonoCompraEntity(
                    idAbonoCompra = abono.id,
                    monto = amount,
                    fechaAbono = System.currentTimeMillis()
                )
                detalleAbonoCompraDao.insert(abonoDetalleEntity)
                "Pago realizado con éxito"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }

    override suspend fun updatePurchaseDetail(
        purchaseDetail: DetalleCompraEntity,
        newTotal: Double
    ): String {
        return runCatching {
            appDatabase.withTransaction {

                // Actualizar el nuevo total a abonar y el total pendiente correspondiente
                val abono = compraAbonoDao.getAbonoByPurchaseId(purchaseDetail.idCompra)
                val abonos = detalleAbonoCompraDao.getAllByAbonoId(abono.id)

                compraAbonoDao.update(
                    abono.copy(
                        totalPendiente = newTotal - abonos.sumOf { it.monto },
                        totalAPagar = newTotal
                    )
                )

                // Actualizar el total de la factura
                val purchase = purchaseDao.getPurchaseById(purchaseDetail.idCompra)
                purchaseDao.update(
                    purchase.copy(
                        total = newTotal,
                    )
                )

                purchaseDetailDao.update(purchaseDetail)
                "Detalle actualizado con éxito"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }

    override suspend fun deletePurchaseDetail(
        purchaseDetail: DetalleCompraEntity,
        newTotal: Double
    ): String {
        runCatching {

            // Eliminar el detalle de la compra
            purchaseDetailDao.delete(purchaseDetail.id)

            // Actualizar el nuevo total a abonar y el total pendiente correspondiente
            val abono = compraAbonoDao.getAbonoByPurchaseId(purchaseDetail.idCompra)
            val abonos = detalleAbonoCompraDao.getAllByAbonoId(abono.id)

            compraAbonoDao.update(
                abono.copy(
                    totalPendiente = newTotal - abonos.sumOf { it.monto },
                    totalAPagar = newTotal
                )
            )

            // Actualizar el total de la factura
            val purchase = purchaseDao.getPurchaseById(purchaseDetail.idCompra)
            purchaseDao.update(
                purchase.copy(
                    total = newTotal,
                )
            )

            return "Detalle eliminado con éxito"
        }.getOrElse {
            return "Error: ${it.message}"
        }
    }
}