package com.example.facturacionelunico.data.mappers

import com.example.facturacionelunico.data.database.entities.ProductoEntity
import com.example.facturacionelunico.domain.models.ProductDomainModel

object ProductMapper {

    // Convertir de Entity (Data) a Domain
    fun toDomain(entity: ProductoEntity): ProductDomainModel {
        return ProductDomainModel(
            id = entity.id,
            name = entity.nombre,
            idBrand = entity.idMarca,
            idCategory = entity.idCategoria,
            description = entity.descripcion,
            photo = entity.foto,
            stock = entity.stock,
            priceSell = entity.precioVenta,
            priceBuy = entity.precioCompra
        )
    }

    // Convertir de Domain a Entity (Data)
    fun toEntity(domain: ProductDomainModel): ProductoEntity {
        return ProductoEntity(
            id = domain.id,
            nombre = domain.name,
            idMarca = domain.idBrand,
            idCategoria = domain.idCategory,
            descripcion = domain.description,
            foto = domain.photo,
            stock = domain.stock,
            precioVenta = domain.priceSell,
            precioCompra = domain.priceBuy
        )
    }
}