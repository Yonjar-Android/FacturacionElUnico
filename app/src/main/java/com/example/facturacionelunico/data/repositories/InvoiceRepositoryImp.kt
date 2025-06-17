package com.example.facturacionelunico.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.facturacionelunico.data.database.AppDatabase
import com.example.facturacionelunico.data.database.dao.AbonoVentaDao
import com.example.facturacionelunico.data.database.dao.DetalleAbonoVentaDao
import com.example.facturacionelunico.data.database.dao.DetalleVentaDao
import com.example.facturacionelunico.data.database.dao.ProductoDao
import com.example.facturacionelunico.data.database.dao.VentaDao
import com.example.facturacionelunico.data.database.entities.AbonoVentaEntity
import com.example.facturacionelunico.data.database.entities.DetalleAbonoVentaEntity
import com.example.facturacionelunico.data.database.entities.DetalleVentaEntity
import com.example.facturacionelunico.data.database.entities.VentaEntity
import com.example.facturacionelunico.domain.models.ProductItem
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.invoice.DetailInvoiceDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDetailDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import com.example.facturacionelunico.domain.repositories.InvoiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvoiceRepositoryImp @Inject constructor(
    private val invoiceDao: VentaDao,
    private val detailInvoiceDao: DetalleVentaDao,
    private val abonosDao: AbonoVentaDao,
    private val abonosDetalleDao: DetalleAbonoVentaDao,
    private val productoDao: ProductoDao,
    private val appDatabase: AppDatabase
) : InvoiceRepository {
    override fun getInvoices(): Flow<PagingData<InvoiceDomainModel>> {
        return runCatching {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 5
                ),
                pagingSourceFactory = { invoiceDao.getAll() }
            ).flow
                .map { pagingData ->
                    pagingData.map {
                        InvoiceDomainModel(
                            id = it.id,
                            sellDate = it.fechaVenta,
                            total = it.total,
                            clientId = it.idCliente,
                            state = it.estado,
                            paymentMethod = it.tipoPago
                        )
                    }

                }
        }.getOrElse {
            flow { emit(PagingData.empty()) }
        }
    }

    override fun getInvoicesWithDebt(): Flow<PagingData<InvoiceDomainModel>> {
        return runCatching {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 5
                ),
                pagingSourceFactory = { invoiceDao.getInvoicesWithDebt() }
            ).flow
                .map { pagingData ->
                    pagingData.map {
                        InvoiceDomainModel(
                            id = it.id,
                            sellDate = it.fechaVenta,
                            total = it.total,
                            clientId = it.idCliente,
                            state = it.estado,
                            paymentMethod = it.tipoPago
                        )
                    }

                }
        }.getOrElse {
            flow { emit(PagingData.empty()) }
        }
    }

    override suspend fun createInvoice(
        invoice: InvoiceDomainModel,
        details: List<DetailInvoiceDomainModel>,
        moneyPaid: Double
    ): String {

        return runCatching {
            appDatabase.withTransaction {
                val invoiceEntity = VentaEntity(
                    fechaVenta = invoice.sellDate,
                    total = invoice.total,
                    idCliente = invoice.clientId,
                    estado = invoice.state,
                    tipoPago = invoice.paymentMethod
                )

                val id = invoiceDao.insert(invoiceEntity)

                val validate = stockValidation(details)

                if (validate.isNotEmpty()) {
                    throw IllegalArgumentException(validate)
                }

                details.forEach {
                    val detailEntity = DetalleVentaEntity(
                        idVenta = id,
                        idProducto = it.productId,
                        cantidad = it.quantity,
                        precio = it.price,
                        subtotal = it.subtotal,
                        fechaActualizacion = invoice.sellDate,
                        precioCompra = it.purchasePrice
                    )
                    detailInvoiceDao.insert(detailEntity)
                }

                val abonoEntity = AbonoVentaEntity(
                    idVenta = id,
                    fechaCreacion = invoice.sellDate,
                    totalAPagar = invoice.total,
                    totalPendiente = invoice.total
                )

                val idAbono = abonosDao.insert(abonoEntity)

                if (moneyPaid > 0) {
                    val abonoDetalleEntity = DetalleAbonoVentaEntity(
                        idAbonoVenta = idAbono,
                        monto = moneyPaid,
                        fechaAbono = System.currentTimeMillis()
                    )
                    abonosDetalleDao.insert(abonoDetalleEntity)
                }
                "Factura creada con éxito"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }

    override suspend fun createInvoiceDetail(
        invoiceId: Long,
        products: List<ProductItem>
    ): String {
        return runCatching {
            appDatabase.withTransaction { // Permite que Si una sola operación falla, se revierte todo.
                products.forEach {
                    val detailEntity = DetalleVentaEntity(
                        idVenta = invoiceId,
                        idProducto = it.id,
                        cantidad = it.quantity,
                        precio = it.price,
                        subtotal = it.subtotal,
                        fechaActualizacion = System.currentTimeMillis(),
                        precioCompra = it.purchasePrice
                    )

                    val validate = stockValidationOneProduct(
                        productId = it.id,
                        quantity = it.quantity
                    )

                    if (validate.isNotEmpty()) {
                        throw IllegalArgumentException(validate)
                    }

                    detailInvoiceDao.insert(detailEntity)
                }

                val newTotal = products.sumOf { it.subtotal }
                val invoice = invoiceDao.getInvoiceById(invoiceId)

                invoiceDao.update(
                    invoice.copy(
                        total = invoice.total + newTotal,
                        estado = "PENDIENTE"
                    )
                )

                val abono = abonosDao.getAbonoByInvoiceId(invoiceId)

                abonosDao.update(
                    abono.copy(
                        totalPendiente = abono.totalPendiente + newTotal,
                        totalAPagar = abono.totalAPagar + newTotal
                    )
                )
            }

            "Factura actualizada con éxito"
        }.getOrElse {
            "Error: ${it.message}"
        }
    }

    override fun getInvoiceDetailById(id: Long): Flow<ResultPattern<InvoiceDetailDomainModel>> {
        val invoiceFlow = invoiceDao.getInvoiceDetailById(id)
        val productsFlow = detailInvoiceDao.getDetailsByInvoiceId(id)

        return combine(invoiceFlow, productsFlow) { invoice, products ->
            runCatching {
                ResultPattern.Success(
                    InvoiceDetailDomainModel(
                        invoiceId = invoice.idFactura,
                        clientName = if (invoice.nombreCliente.isNullOrEmpty()) "Ninguno" else "${invoice.nombreCliente} ${invoice.apellidoCliente}",
                        total = invoice.totalFactura,
                        debt = invoice.totalPendiente,
                        products = products
                    )
                )

            }.getOrElse { e ->
                ResultPattern.Error(exception = e, message = "Error: ${e.message}")
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun payInvoice(invoiceId: Long, amount: Double): String {
        return runCatching {

            val abono = abonosDao.getAbonoByInvoiceId(invoiceId)

            if (abono.totalPendiente < amount) {
                "Error: Se ha intentado pagar más de lo que se debe"
            } else {

                val abonoDetalleEntity = DetalleAbonoVentaEntity(
                    idAbonoVenta = abono.id,
                    monto = amount,
                    fechaAbono = System.currentTimeMillis()
                )

                abonosDetalleDao.insert(abonoDetalleEntity)
                "Pago realizado con éxito"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }

    override suspend fun updateInvoiceDetail(
        newDetail: DetalleVentaEntity,
        newTotal: Double
    ): String {
        return runCatching {
            appDatabase.withTransaction {


                val abono = abonosDao.getAbonoByInvoiceId(newDetail.idVenta)
                val abonos = abonosDetalleDao.getAllByAbonoId(abono.id)

                val invoiceDetail = detailInvoiceDao.getByInvoiceDetailId(newDetail.id)

                // Si la nueva cantidad de producto a adquirir es mayor que la anterior
                // Entonces comprobar que haya suficiente stock para realizar la venta
                if (newDetail.cantidad > invoiceDetail.cantidad){
                    val validate = stockValidationOneProduct(newDetail.idProducto, newDetail.cantidad - invoiceDetail.cantidad)
                    if (validate.isNotEmpty()) {
                        throw IllegalArgumentException("No hay suficiente stock para realizar la actualización")
                    }
                }

                if (newTotal < abonos.sumOf { it.monto }) {
                    throw IllegalArgumentException("El nuevo total es menor a la cantidad ya abonada")
                }

                // Actualizar el nuevo total a abonar y el total pendiente correspondiente
                abonosDao.update(
                    abono.copy(
                        totalPendiente = newTotal - abonos.sumOf { it.monto },
                        totalAPagar = newTotal
                    )
                )



                // Actualizar el total de la factura
                val invoice = invoiceDao.getInvoiceById(newDetail.idVenta)
                invoiceDao.update(
                    invoice.copy(
                        total = newTotal,
                    )
                )

                detailInvoiceDao.update(newDetail)
                "Detalle actualizado con éxito"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }

    override suspend fun deleteInvoiceDetail(
        invoiceDetail: DetalleVentaEntity,
        newTotal: Double
    ): String {
        return runCatching {
            appDatabase.withTransaction {
                // Eliminar el detalle de la venta
                detailInvoiceDao.delete(invoiceDetail.id)

                // Actualizar el nuevo total a abonar y el total pendiente correspondiente
                val abono = abonosDao.getAbonoByInvoiceId(invoiceDetail.idVenta)
                val abonos = abonosDetalleDao.getAllByAbonoId(abono.id)

                if(newTotal < abonos.sumOf { it.monto }){
                    throw IllegalArgumentException("El nuevo total es menor a la cantidad ya abonada")
                }

                abonosDao.update(
                    abono.copy(
                        totalPendiente = newTotal - abonos.sumOf { it.monto },
                        totalAPagar = newTotal
                    )
                )

                // Actualizar el total de la factura
                val invoice = invoiceDao.getInvoiceById(invoiceDetail.idVenta)
                invoiceDao.update(
                    invoice.copy(
                        total = newTotal,
                    )
                )
                "Detalle eliminado con éxito"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }

    suspend fun stockValidation(products: List<DetailInvoiceDomainModel>): String {
        products.forEach { productDetail ->
            val product = productoDao.getDetailedById(productDetail.productId)
            product?.stock.let { stock ->
                if (stock!! < productDetail.quantity) {
                    return "No hay suficiente stock de ${product?.name}. Stock: ${product?.stock}"
                }
            }
        }
        return ""
    }

    suspend fun stockValidationOneProduct(productId: Long, quantity: Int): String {
        val product = productoDao.getDetailedById(productId)
        product?.stock?.let {
            if (it < quantity) {
                return "No hay suficiente stock de ${product.name}. Stock: ${product.stock}"
            }
        }
        return ""
    }
}