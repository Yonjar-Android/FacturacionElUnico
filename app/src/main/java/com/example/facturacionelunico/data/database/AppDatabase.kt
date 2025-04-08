package com.example.facturacionelunico.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.facturacionelunico.data.database.dao.AbonoCompraDao
import com.example.facturacionelunico.data.database.dao.AbonoVentaDao
import com.example.facturacionelunico.data.database.dao.CategoriaDao
import com.example.facturacionelunico.data.database.dao.ClienteDao
import com.example.facturacionelunico.data.database.dao.CompraDao
import com.example.facturacionelunico.data.database.dao.DetalleAbonoCompraDao
import com.example.facturacionelunico.data.database.dao.DetalleAbonoVentaDao
import com.example.facturacionelunico.data.database.dao.DetalleCompraDao
import com.example.facturacionelunico.data.database.dao.DetalleDevolucionDao
import com.example.facturacionelunico.data.database.dao.DetalleVentaDao
import com.example.facturacionelunico.data.database.dao.DevolucionDao
import com.example.facturacionelunico.data.database.dao.MarcaDao
import com.example.facturacionelunico.data.database.dao.ProductoDao
import com.example.facturacionelunico.data.database.dao.ProveedorDao
import com.example.facturacionelunico.data.database.dao.VentaDao
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

@Database(
    entities = [
        ClienteEntity::class,
        MarcaEntity::class,
        CategoriaEntity::class,
        ProductoEntity::class,
        VentaEntity::class,
        DetalleVentaEntity::class,
        CompraEntity::class,
        DetalleCompraEntity::class,
        ProveedorEntity::class,
        DevolucionEntity::class,
        DetalleDevolucionEntity::class,
        AbonoCompraEntity::class,
        DetalleAbonoCompraEntity::class,
        AbonoVentaEntity::class,
        DetalleAbonoVentaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun marcaDao(): MarcaDao

    abstract fun ventaDao(): VentaDao
    abstract fun detalleVentaDao(): DetalleVentaDao

    abstract fun compraDao(): CompraDao
    abstract fun detalleCompraDao(): DetalleCompraDao

    abstract fun proveedorDao(): ProveedorDao

    abstract fun devolucionDao(): DevolucionDao
    abstract fun detalleDevolucionDao(): DetalleDevolucionDao

    abstract fun abonoCompraDao(): AbonoCompraDao
    abstract fun detalleAbonoCompraDao(): DetalleAbonoCompraDao

    abstract fun abonoVentaDao(): AbonoVentaDao
    abstract fun detalleAbonoVentaDao(): DetalleAbonoVentaDao
}
