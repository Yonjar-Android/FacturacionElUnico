package com.example.facturacionelunico.presentation.productScreenTab.productScreen

import com.example.facturacionelunico.data.mappers.ProductMapper
import com.example.facturacionelunico.data.repositories.MotherObjectRepositories
import com.example.facturacionelunico.domain.models.DetailedProductModel

object MotherObjectProduct {

    val product = ProductMapper.toDomain(MotherObjectRepositories.oneProductEntity)

    val productDetailed = DetailedProductModel(
        id = 201,
        name = "Kenda Kaiser KR20 - Llanta 205/55R16",
        category = "Llantas para Autos",
        categoryId = 1,
        brand = "Kenda",
        brandId = 10,
        salePrice = 320.00,
        purchasePrice = 210.50,
        stock = 18,
        description = "Llanta radial premium con banda de rodadura silenciosa y eficiente en combustible. Índice de velocidad V (240 km/h)",
        photo = "https://cdn.ejemplo.com/llantas/kenda_kaiser_kr20.jpg"
    )

}