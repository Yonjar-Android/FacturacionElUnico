package com.example.facturacionelunico.di

import com.example.facturacionelunico.data.database.dao.CategoriaDao
import com.example.facturacionelunico.data.database.dao.MarcaDao
import com.example.facturacionelunico.data.repositories.BrandRepositoryImp
import com.example.facturacionelunico.data.repositories.CategoryRepositoryImp
import com.example.facturacionelunico.domain.repositories.BrandRepository
import com.example.facturacionelunico.domain.repositories.CategoryRepository
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
    ): BrandRepository{
        return BrandRepositoryImp(brandDao)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoriaDao
    ): CategoryRepository{
        return CategoryRepositoryImp(categoryDao)
    }
}