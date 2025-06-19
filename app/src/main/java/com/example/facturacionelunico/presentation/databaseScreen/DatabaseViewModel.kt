package com.example.facturacionelunico.presentation.databaseScreen

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.data.database.AppDatabase
import com.example.facturacionelunico.data.database.entities.AbonoCompraEntity
import com.example.facturacionelunico.data.database.entities.AbonoVentaEntity
import com.example.facturacionelunico.data.database.entities.CategoriaEntity
import com.example.facturacionelunico.data.database.entities.ClienteEntity
import com.example.facturacionelunico.data.database.entities.CompraEntity
import com.example.facturacionelunico.data.database.entities.DetalleAbonoCompraEntity
import com.example.facturacionelunico.data.database.entities.DetalleAbonoVentaEntity
import com.example.facturacionelunico.data.database.entities.DetalleCompraEntity
import com.example.facturacionelunico.data.database.entities.DetalleDevolucionEntity
import com.example.facturacionelunico.data.database.entities.DetalleVentaEntity
import com.example.facturacionelunico.data.database.entities.DevolucionEntity
import com.example.facturacionelunico.data.database.entities.MarcaEntity
import com.example.facturacionelunico.data.database.entities.ProductoEntity
import com.example.facturacionelunico.data.database.entities.ProveedorEntity
import com.example.facturacionelunico.data.database.entities.VentaEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DatabaseViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDatabase
): ViewModel() {

    private val _exportStatus = MutableStateFlow<Boolean?>(null)
    val exportStatus: StateFlow<Boolean?> = _exportStatus

    private val _importStatus = MutableStateFlow<Boolean?>(null)
    val importStatus: StateFlow<Boolean?> = _importStatus

    fun exportDatabase() {
        viewModelScope.launch {
            val result = exportDataToJson()
            _exportStatus.value = result
        }
    }

    private suspend fun exportDataToJson(): Boolean {
        return try {
            val gson = Gson()

            val backupData = BackupData(
                clientes = db.clienteDao().getAllJson(),
                productos = db.productoDao().getAllJson(),
                categorias = db.categoriaDao().getAllJson(),
                marcas = db.marcaDao().getAllJson(),
                ventas = db.ventaDao().getAllJson(),
                detallesVenta = db.detalleVentaDao().getAll(),
                compras = db.compraDao().getAllJson(),
                detallesCompra = db.detalleCompraDao().getAll(),
                proveedores = db.proveedorDao().getAllJson(),
                devoluciones = db.devolucionDao().getAll(),
                detallesDevolucion = db.detalleDevolucionDao().getAll(),
                abonosCompra = db.abonoCompraDao().getAll(),
                detallesAbonoCompra = db.detalleAbonoCompraDao().getAll(),
                abonosVenta = db.abonoVentaDao().getAll(),
                detallesAbonoVenta = db.detalleAbonoVentaDao().getAll()
            )

            val json = gson.toJson(backupData)

            // Guardar en carpeta privada
            val internalBackupDir = File(context.getExternalFilesDir(null), "backup")
            if (!internalBackupDir.exists()) internalBackupDir.mkdirs()
            File(internalBackupDir, "respaldo_datos.json").writeText(json)

            // Guardar también en carpeta pública accesible (ej: Documents)
            val publicDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "FacturacionElUnico")
            if (!publicDir.exists()) publicDir.mkdirs()
            File(publicDir, "respaldo_datos_${System.currentTimeMillis()}.json").writeText(json)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    fun resetExportStatus() {
        _exportStatus.value = null
    }

    fun importFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            val success = try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val json = inputStream?.bufferedReader().use { it?.readText() }

                val backupData = Gson().fromJson(json, BackupData::class.java)

                // 🔴 Desactiva triggers temporalmente
                deshabilitarTriggers()

                // 🔥 Eliminar todos los datos primero (respetar dependencias)
                db.detalleAbonoVentaDao().deleteAll()
                db.abonoVentaDao().deleteAll()
                db.detalleAbonoCompraDao().deleteAll()
                db.abonoCompraDao().deleteAll()
                db.detalleDevolucionDao().deleteAll()
                db.devolucionDao().deleteAll()
                db.detalleCompraDao().deleteAll()
                db.compraDao().deleteAll()
                db.detalleVentaDao().deleteAll()
                db.ventaDao().deleteAll()
                db.marcaDao().deleteAll()
                db.categoriaDao().deleteAll()
                db.proveedorDao().deleteAll()
                db.clienteDao().deleteAll()
                db.productoDao().deleteAll()

                // Guardar en base de datos
                db.clienteDao().insertAll(backupData.clientes)
                db.productoDao().insertAll(backupData.productos)
                db.categoriaDao().insertAll(backupData.categorias)
                db.marcaDao().insertAll(backupData.marcas)
                db.ventaDao().insertAll(backupData.ventas)
                db.detalleVentaDao().insertAll(backupData.detallesVenta)
                db.compraDao().insertAll(backupData.compras)
                db.detalleCompraDao().insertAll(backupData.detallesCompra)
                db.proveedorDao().insertAll(backupData.proveedores)
                db.devolucionDao().insertAll(backupData.devoluciones)
                db.detalleDevolucionDao().insertAll(backupData.detallesDevolucion)
                db.abonoCompraDao().insertAll(backupData.abonosCompra)
                db.detalleAbonoCompraDao().insertAll(backupData.detallesAbonoCompra)
                db.abonoVentaDao().insertAll(backupData.abonosVenta)
                db.detalleAbonoVentaDao().insertAll(backupData.detallesAbonoVenta)

                // 🟢 Volver a activar triggers
                restaurarTriggers()


                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

            _importStatus.value = success
        }
    }

    suspend fun deshabilitarTriggers() {
        withContext(Dispatchers.IO) {
            db.openHelper.writableDatabase.apply {
                execSQL("DROP TRIGGER IF EXISTS actualizar_stock")
                execSQL("DROP TRIGGER IF EXISTS actualizar_abono_y_estado_venta")
                execSQL("DROP TRIGGER IF EXISTS actualizar_stock_detalle_compra")
                execSQL("DROP TRIGGER IF EXISTS actualizar_stock_al_eliminar_detalle_compra")
                execSQL("DROP TRIGGER IF EXISTS actualizar_stock_detalle_venta")
                execSQL("DROP TRIGGER IF EXISTS actualizar_stock_al_eliminar_detalle_venta")
                execSQL("DROP TRIGGER IF EXISTS actualizar_stock_compra")
                execSQL("DROP TRIGGER IF EXISTS actualizar_abono_y_estado_compra")
            }
        }
    }

    suspend fun restaurarTriggers() {
        withContext(Dispatchers.IO) {
            db.openHelper.writableDatabase.apply {
                execSQL(
                    """
                CREATE TRIGGER actualizar_stock_al_eliminar_detalle_venta
                AFTER DELETE ON detalle_venta
                BEGIN
                    UPDATE producto
                    SET stock = stock + OLD.cantidad
                    WHERE id = OLD.idProducto;
                END;
            """.trimIndent()
                )

                execSQL(
                    """
                CREATE TRIGGER actualizar_stock_detalle_venta
                AFTER UPDATE ON detalle_venta
                BEGIN
                UPDATE producto
                SET stock = stock + OLD.cantidad - NEW.cantidad
                WHERE id = NEW.idProducto;
                END;
            """.trimIndent()
                )

                execSQL(
                    """
                CREATE TRIGGER actualizar_stock_al_eliminar_detalle_compra
                AFTER DELETE ON detalle_compra
                BEGIN
                    UPDATE producto
                    SET stock = stock - OLD.cantidad
                    WHERE id = OLD.idProducto;
                END;
            """.trimIndent()
                )

                execSQL(
                    """
                CREATE TRIGGER actualizar_stock_detalle_compra
                AFTER UPDATE ON detalle_compra
                BEGIN
                    UPDATE producto
                    SET stock = stock + (NEW.cantidad - OLD.cantidad)
                    WHERE id = NEW.idProducto;
                END;
            """.trimIndent()
                )

                execSQL(
                    """
                CREATE TRIGGER actualizar_stock
                AFTER INSERT ON detalle_venta
                BEGIN
                    UPDATE producto
                    SET stock = stock - NEW.cantidad
                    WHERE id = NEW.idProducto;
                END;
            """.trimIndent()
                )

                execSQL(
                    """
                CREATE TRIGGER actualizar_abono_y_estado_venta
                AFTER INSERT ON detalle_abono_venta
                BEGIN
                    UPDATE abono_venta
                    SET totalPendiente = totalPendiente - NEW.monto
                    WHERE id = NEW.idAbonoVenta;

                    UPDATE venta
                    SET estado = 'COMPLETADO'
                    WHERE id IN (
                        SELECT idVenta
                        FROM abono_venta
                        WHERE id = NEW.idAbonoVenta AND totalPendiente <= 0
                    );
                END;
            """.trimIndent()
                )

                // Trigger para stock de productos al hacer una compra
                execSQL(
                    """
            CREATE TRIGGER actualizar_stock_compra
            AFTER INSERT ON detalle_compra
            BEGIN
                UPDATE producto
                SET stock = stock + NEW.cantidad
                WHERE id = NEW.idProducto;
            END;
        """.trimIndent()
                )

                // Trigger para actualizar abono y estado de compra
                execSQL(
                    """
            CREATE TRIGGER actualizar_abono_y_estado_compra
            AFTER INSERT ON detalle_abono_compra
            BEGIN
                UPDATE abono_compra
                SET totalPendiente = totalPendiente - NEW.monto
                WHERE id = NEW.idAbonoCompra;

                UPDATE compra
                SET estado = 'COMPLETADO'
                WHERE id IN (
                    SELECT idCompra
                    FROM abono_compra
                    WHERE id = NEW.idAbonoCompra AND totalPendiente <= 0
                );
            END;
        """.trimIndent()
                )
            }
        }
    }



    fun resetImportStatus() {
        _importStatus.value = null
    }
}

data class BackupData(
    val clientes: List<ClienteEntity>,
    val productos: List<ProductoEntity>,
    val categorias: List<CategoriaEntity>,
    val marcas: List<MarcaEntity>,

    val ventas: List<VentaEntity>,
    val detallesVenta: List<DetalleVentaEntity>,

    val compras: List<CompraEntity>,
    val detallesCompra: List<DetalleCompraEntity>,

    val proveedores: List<ProveedorEntity>,

    val devoluciones: List<DevolucionEntity>,
    val detallesDevolucion: List<DetalleDevolucionEntity>,

    val abonosCompra: List<AbonoCompraEntity>,
    val detallesAbonoCompra: List<DetalleAbonoCompraEntity>,

    val abonosVenta: List<AbonoVentaEntity>,
    val detallesAbonoVenta: List<DetalleAbonoVentaEntity>
)
