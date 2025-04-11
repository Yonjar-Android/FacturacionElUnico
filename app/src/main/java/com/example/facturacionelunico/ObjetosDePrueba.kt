package com.example.facturacionelunico

import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.ProductDomainModel

object ObjetosDePrueba {
    val motorcycleProducts = listOf(
        ProductDomainModel(
            productId = 1L,
            productName = "Llanta Yebram 3.00-18",
            brand = "Yebram",
            price = 1200f,
            stock = 20
        ),
        ProductDomainModel(
            productId = 2L,
            productName = "Aceite Motul 7100 10W40",
            brand = "Motul",
            price = 950f,
            stock = 15
        ),
        ProductDomainModel(
            productId = 3L,
            productName = "Cadena DID 428HD",
            brand = "DID",
            price = 800f,
            stock = 10
        ),
        ProductDomainModel(
            productId = 4L,
            productName = "Batería Yuasa YTX7A-BS",
            brand = "Yuasa",
            price = 1500f,
            stock = 8
        ),
        ProductDomainModel(
            productId = 5L,
            productName = "Pastillas de Freno Galfer",
            brand = "Galfer",
            price = 400f,
            stock = 25
        )
    )

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