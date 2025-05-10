package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.AbonoVentaDao
import com.example.facturacionelunico.data.database.dao.DetalleAbonoVentaDao
import com.example.facturacionelunico.data.database.dao.DetalleVentaDao
import com.example.facturacionelunico.data.database.dao.ProductoDao
import com.example.facturacionelunico.data.database.dao.VentaDao
import com.example.facturacionelunico.data.database.entities.AbonoVentaEntity
import com.example.facturacionelunico.data.database.entities.DetalleAbonoVentaEntity
import com.example.facturacionelunico.data.database.entities.DetalleVentaEntity
import com.example.facturacionelunico.data.database.entities.VentaEntity
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.invoice.DetailInvoiceDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDetailDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import com.example.facturacionelunico.domain.repositories.InvoiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class InvoiceRepositoryImp @Inject constructor(
    private val invoiceDao: VentaDao,
    private val detailInvoiceDao: DetalleVentaDao,
    private val abonosDao: AbonoVentaDao,
    private val abonosDetalleDao: DetalleAbonoVentaDao,
    private val productoDao: ProductoDao
) : InvoiceRepository {
    override suspend fun getInvoices(): List<InvoiceDomainModel> {
        return runCatching {
            invoiceDao.getAll().map {
                InvoiceDomainModel(
                    id = it.id,
                    sellDate = it.fechaVenta,
                    total = it.total,
                    clientId = it.idCliente,
                    state = it.estado
                )
            }
        }.getOrElse {
            emptyList()
        }
    }

    override suspend fun createInvoice(
        invoice: InvoiceDomainModel,
        details: List<DetailInvoiceDomainModel>,
        moneyPaid: Double
    ): String {

        return runCatching {
            val invoiceEntity = VentaEntity(
                fechaVenta = invoice.sellDate,
                total = invoice.total,
                idCliente = invoice.clientId,
                estado = invoice.state
            )

            val id = invoiceDao.insert(invoiceEntity)

            val validate = stockValidation(details)

            if (validate.contains("Error")) {
                return@runCatching validate
            }

            details.forEach {
                val detailEntity = DetalleVentaEntity(
                    idVenta = id,
                    idProducto = it.productId,
                    cantidad = it.quantity,
                    precio = it.price,
                    subtotal = it.subtotal
                )
                detailInvoiceDao.insert(detailEntity)
            }

            // Condicional donde si el pago es PENDIENTE se cree el abono del cliente
            if (invoice.state == "PENDIENTE") {
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
            }

            "Factura creada con éxito"

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

    suspend fun stockValidation(products: List<DetailInvoiceDomainModel>): String {
        products.forEach {
            val product = productoDao.getDetailedById(it.productId)
            return "Error: No hay suficiente stock de ${product?.name}. Stock: ${product?.stock}"
        }
        return ""
    }
}