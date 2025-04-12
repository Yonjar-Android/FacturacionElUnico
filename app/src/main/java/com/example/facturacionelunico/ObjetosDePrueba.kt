package com.example.facturacionelunico

import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.ProductDomainModel

object ObjetosDePrueba {
    val motorcycleProducts = emptyList<ProductDomainModel>()

    val motorcycleCategories = listOf(
        CategoryDomainModel(categoryId = 1L, categoryName = "Llantas"),
        CategoryDomainModel(categoryId = 2L, categoryName = "Aceites y Lubricantes"),
        CategoryDomainModel(categoryId = 3L, categoryName = "Frenos"),
        CategoryDomainModel(categoryId = 4L, categoryName = "Baterías"),
        CategoryDomainModel(categoryId = 5L, categoryName = "Accesorios")
    )

    val motorcycleBrands = listOf(
        BrandDomainModel(brandId = 1L, brandName = "Motul"),
        BrandDomainModel(brandId = 2L, brandName = "Yuasa"),
        BrandDomainModel(brandId = 3L, brandName = "Pirelli"),
        BrandDomainModel(brandId = 4L, brandName = "Galfer"),
        BrandDomainModel(brandId = 5L, brandName = "DID")
    )



}