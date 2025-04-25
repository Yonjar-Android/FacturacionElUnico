package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.entities.CategoriaEntity
import com.example.facturacionelunico.data.database.entities.MarcaEntity
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object MotherObjectRepositories {

    private val brandsEntities = listOf<MarcaEntity>(
        MarcaEntity(id = 1L, nombre = "Nike"),
        MarcaEntity(id = 2L, nombre = "Adidas")
    )

    val flowBrands: Flow<List<MarcaEntity>> = flow { emit(brandsEntities) }

    val flowBrandName: Flow<List<MarcaEntity>> =
        flow { emit(listOf(MarcaEntity(id = 1L, nombre = "Nike"))) }

    private val brandDomainModel = BrandDomainModel(brandId = 1L, brandName = "Nike")

    private val brandEntity = MarcaEntity(id = 1L, nombre = "Nike")

    val flowBrandEntity = flow { emit(brandEntity) }

    val flowBrandDomainModel: Flow<BrandDomainModel> = flow { emit(brandDomainModel) }

        val products = listOf<DetailedProductModel>(
            DetailedProductModel(
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
            ),
            DetailedProductModel(
                id = 202,
                name = "Kenda All-Terrain - Llanta 265/70R17",
                category = "Llantas para Camionetas",
                categoryId = 2,
                brand = "Kenda", // Marca conjunta
                brandId = 10, // ID hipotético para alianza de marcas
                salePrice = 480.00,
                purchasePrice = 310.00,
                stock = 12,
                description = "Llanta todoterreno co-desarrollada por Kenda. Resistente a cortes con tecnología Kevlar® y diseño auto-limpiante",
                photo = "https://cdn.ejemplo.com/llantas/kenda_yebram_at.jpg"
            )
        )

    val categoriaLLantas =CategoriaEntity(id = 1L, nombre = "Llantas")

    val flowOneCategory = flow { emit(categoriaLLantas) }

    val listCategoryEntities = listOf<CategoriaEntity>(
        categoriaLLantas,
        CategoriaEntity(id = 2L, nombre = "Aceites")
    )

    val flowCategoryEntities: Flow<List<CategoriaEntity>> = flow { emit(listCategoryEntities) }

    }