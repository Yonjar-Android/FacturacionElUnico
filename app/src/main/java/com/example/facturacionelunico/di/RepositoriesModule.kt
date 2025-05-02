package com.example.facturacionelunico.di

import com.example.facturacionelunico.data.database.dao.CategoriaDao
import com.example.facturacionelunico.data.database.dao.ClienteDao
import com.example.facturacionelunico.data.database.dao.MarcaDao
import com.example.facturacionelunico.data.database.dao.ProductoDao
import com.example.facturacionelunico.data.database.dao.ProveedorDao
import com.example.facturacionelunico.data.repositories.BrandRepositoryImp
import com.example.facturacionelunico.data.repositories.CategoryRepositoryImp
import com.example.facturacionelunico.data.repositories.ClientRepositoryImp
import com.example.facturacionelunico.data.repositories.ProductRepositoryImp
import com.example.facturacionelunico.data.repositories.SupplierRepositoryImp
import com.example.facturacionelunico.domain.repositories.BrandRepository
import com.example.facturacionelunico.domain.repositories.CategoryRepository
import com.example.facturacionelunico.domain.repositories.ClientRepository
import com.example.facturacionelunico.domain.repositories.ProductRepository
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
        clientDao: ClienteDao
    ): ClientRepository {
        return ClientRepositoryImp(clientDao)
    }

    @Provides
    @Singleton
    fun provideSupplierRepository(
        supplierDao: ProveedorDao
    ): SupplierRepository {
        return SupplierRepositoryImp(supplierDao)
    }
}