package com.example.facturacionelunico.di

import com.example.facturacionelunico.data.database.AppDatabase
import com.example.facturacionelunico.data.database.dao.AbonoCompraDao
import com.example.facturacionelunico.data.database.dao.AbonoVentaDao
import com.example.facturacionelunico.data.database.dao.CategoriaDao
import com.example.facturacionelunico.data.database.dao.ClienteDao
import com.example.facturacionelunico.data.database.dao.CompraDao
import com.example.facturacionelunico.data.database.dao.DetalleAbonoCompraDao
import com.example.facturacionelunico.data.database.dao.DetalleAbonoVentaDao
import com.example.facturacionelunico.data.database.dao.DetalleCompraDao
import com.example.facturacionelunico.data.database.dao.DetalleVentaDao
import com.example.facturacionelunico.data.database.dao.MarcaDao
import com.example.facturacionelunico.data.database.dao.ProductoDao
import com.example.facturacionelunico.data.database.dao.ProveedorDao
import com.example.facturacionelunico.data.database.dao.VentaDao
import com.example.facturacionelunico.data.repositories.BrandRepositoryImp
import com.example.facturacionelunico.data.repositories.CategoryRepositoryImp
import com.example.facturacionelunico.data.repositories.ClientRepositoryImp
import com.example.facturacionelunico.data.repositories.InvoiceRepositoryImp
import com.example.facturacionelunico.data.repositories.ProductRepositoryImp
import com.example.facturacionelunico.data.repositories.PurchaseRepositoryImp
import com.example.facturacionelunico.data.repositories.SupplierRepositoryImp
import com.example.facturacionelunico.domain.repositories.BrandRepository
import com.example.facturacionelunico.domain.repositories.CategoryRepository
import com.example.facturacionelunico.domain.repositories.ClientRepository
import com.example.facturacionelunico.domain.repositories.InvoiceRepository
import com.example.facturacionelunico.domain.repositories.ProductRepository
import com.example.facturacionelunico.domain.repositories.PurchaseRepository
import com.example.facturacionelunico.domain.repositories.SupplierRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    @Singleton
    fun provideBrandRepository(
        brandDao: MarcaDao
    ): BrandRepository {
        return BrandRepositoryImp(brandDao)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoriaDao
    ): CategoryRepository {
        return CategoryRepositoryImp(categoryDao)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        productDao: ProductoDao
    ): ProductRepository {
        return ProductRepositoryImp(productDao)
    }

    @Provides
    @Singleton
    fun provideClientRepository(
        clientDao: ClienteDao,
        invoiceDao: VentaDao
    ): ClientRepository {
        return ClientRepositoryImp(clientDao, invoiceDao)
    }

    @Provides
    @Singleton
    fun provideSupplierRepository(
        supplierDao: ProveedorDao,
        purchaseDao: CompraDao
    ): SupplierRepository {
        return SupplierRepositoryImp(supplierDao,
            purchaseDao)
    }

    @Provides
    @Singleton
    fun provideInvoiceRepository(
        ventaDao: VentaDao,
        detalleVentaDao: DetalleVentaDao,
        abonoVentaDao: AbonoVentaDao,
        detalleAbonoVentaDao: DetalleAbonoVentaDao,
        productoDao: ProductoDao,
        appDatabase: AppDatabase
    ): InvoiceRepository {
        return InvoiceRepositoryImp(
            ventaDao,
            detalleVentaDao,
            abonoVentaDao,
            detalleAbonoVentaDao,
            productoDao = productoDao,
            appDatabase = appDatabase
        )
    }

    @Provides
    @Singleton
    fun providePurchaseRepository(
        appDatabase: AppDatabase,
        purchaseDao: CompraDao,
        purchaseDetailDao: DetalleCompraDao,
        compraAbonoDao: AbonoCompraDao,
        detalleAbonoCompraDao: DetalleAbonoCompraDao): PurchaseRepository {
        return PurchaseRepositoryImp(
            appDatabase,
            purchaseDao,
            purchaseDetailDao,
            compraAbonoDao,
            detalleAbonoCompraDao)
    }
}