package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.AbonoVentaDao
import com.example.facturacionelunico.data.database.dao.ClienteDao
import com.example.facturacionelunico.data.database.dao.DetalleAbonoVentaDao
import com.example.facturacionelunico.data.database.dao.DetalleVentaDao
import com.example.facturacionelunico.data.database.dao.ProductoDao
import com.example.facturacionelunico.data.database.dao.VentaDao
import com.example.facturacionelunico.data.database.entities.AbonoVentaEntity
import com.example.facturacionelunico.data.database.entities.DetalleAbonoVentaEntity
import com.example.facturacionelunico.data.database.entities.DetalleVentaEntity
import com.example.facturacionelunico.data.database.entities.VentaEntity
import com.example.facturacionelunico.domain.models.invoice.DetailInvoiceDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import com.example.facturacionelunico.domain.repositories.InvoiceRepository
import javax.inject.Inject

class InvoiceRepositoryImp @Inject constructor(
    private val invoiceDao: VentaDao,
    private val detailInvoiceDao: DetalleVentaDao,
    private val abonosDao: AbonoVentaDao,
    private val abonosDetalleDao: DetalleAbonoVentaDao,
): InvoiceRepository {
    override suspend fun getInvoices() {
        println(invoiceDao.getAll())
        println(detailInvoiceDao.getAll())
    }

    override suspend fun createInvoice(
        invoice: InvoiceDomainModel,
        details: List<DetailInvoiceDomainModel>,
        moneyPaid: Double): String {

        return runCatching {
            val invoiceEntity = VentaEntity(
                fechaVenta = invoice.sellDate,
                total = invoice.total,
                idCliente = invoice.clientId,
                estado = invoice.state
            )

            val id = invoiceDao.insert(invoiceEntity)

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
            if (invoice.state == "PENDIENTE"){
                val abonoEntity = AbonoVentaEntity(
                    idVenta = id,
                    fechaCreacion = invoice.sellDate,
                    totalAPagar = invoice.total,
                    totalPendiente = invoice.total
                )
                println("total ${invoice.total}")
                println("pagado $moneyPaid")
                println(invoice.total - moneyPaid)
                val idAbono = abonosDao.insert(abonoEntity)

                if (moneyPaid > 0){
                    val abonoDetalleEntity = DetalleAbonoVentaEntity(
                        idAbonoVenta = idAbono,
                        monto = moneyPaid,
                        fechaAbono = System.currentTimeMillis()
                    )
                    abonosDetalleDao.insert(abonoDetalleEntity)
                }
            }

            println(invoiceDao.getAll())
            println(detailInvoiceDao.getAll())
            println(abonosDao.getAll())
            println(abonosDetalleDao.getAll())
            "Factura creada con éxito"

        }.getOrElse {
            "Error: ${it.message}"
        }
    }
}