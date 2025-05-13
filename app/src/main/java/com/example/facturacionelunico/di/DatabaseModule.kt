package com.example.facturacionelunico.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.facturacionelunico.data.database.AppDatabase
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // Proporcionar la instancia de AppDatabase
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "elunico_db" // Nombre de la base de datos
        )
            //.addMigrations(DatabaseMigrations.MIGRATION_8_9)
            .addCallback(DatabaseMigrations.dbCallback)
            .build()
    }

    // Proporcionar los DAOs
    @Provides
    @Singleton
    fun provideClienteDao(appDatabase: AppDatabase): ClienteDao {
        return appDatabase.clienteDao()
    }

    @Provides
    @Singleton
    fun provideProductoDao(appDatabase: AppDatabase): ProductoDao {
        return appDatabase.productoDao()
    }

    @Provides
    @Singleton
    fun provideCategoriaDao(appDatabase: AppDatabase): CategoriaDao {
        return appDatabase.categoriaDao()
    }

    @Provides
    @Singleton
    fun provideMarcaDao(appDatabase: AppDatabase): MarcaDao {
        return appDatabase.marcaDao()
    }

    @Provides
    @Singleton
    fun provideVentaDao(appDatabase: AppDatabase): VentaDao {
        return appDatabase.ventaDao()
    }

    @Provides
    @Singleton
    fun provideDetalleVentaDao(appDatabase: AppDatabase): DetalleVentaDao {
        return appDatabase.detalleVentaDao()
    }

    @Provides
    @Singleton
    fun provideCompraDao(appDatabase: AppDatabase): CompraDao {
        return appDatabase.compraDao()
    }

    @Provides
    @Singleton
    fun provideDetalleCompraDao(appDatabase: AppDatabase): DetalleCompraDao {
        return appDatabase.detalleCompraDao()
    }

    @Provides
    @Singleton
    fun provideProveedorDao(appDatabase: AppDatabase): ProveedorDao {
        return appDatabase.proveedorDao()
    }

    @Provides
    @Singleton
    fun provideDevolucionDao(appDatabase: AppDatabase): DevolucionDao {
        return appDatabase.devolucionDao()
    }

    @Provides
    @Singleton
    fun provideDetalleDevolucionDao(appDatabase: AppDatabase): DetalleDevolucionDao {
        return appDatabase.detalleDevolucionDao()
    }

    @Provides
    @Singleton
    fun provideAbonoCompraDao(appDatabase: AppDatabase): AbonoCompraDao {
        return appDatabase.abonoCompraDao()
    }

    @Provides
    @Singleton
    fun provideDetalleAbonoCompraDao(appDatabase: AppDatabase): DetalleAbonoCompraDao {
        return appDatabase.detalleAbonoCompraDao()
    }

    @Provides
    @Singleton
    fun provideAbonoVentaDao(appDatabase: AppDatabase): AbonoVentaDao {
        return appDatabase.abonoVentaDao()
    }

    @Provides
    @Singleton
    fun provideDetalleAbonoVentaDao(appDatabase: AppDatabase): DetalleAbonoVentaDao {
        return appDatabase.detalleAbonoVentaDao()
    }
}
