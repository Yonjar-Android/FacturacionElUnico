package com.example.facturacionelunico.data.repositories.motherObjects

import com.example.facturacionelunico.data.database.entities.ClienteEntity
import com.example.facturacionelunico.data.database.entities.ProveedorEntity
import com.example.facturacionelunico.domain.models.ClientDomainModel
import com.example.facturacionelunico.domain.models.SupplierDomainModel

object MotherObjectClientSuppliers {

    /*Variables a utilizar en el testing del repositorio de clientes y viewModels*/

    val oneDomainClient = ClientDomainModel(
        id = 1L,
        name = "José",
        lastName = "López",
        phone = "3216549870",
        numberIdentifier = 30
    )

    val listDomainClient = listOf<ClientDomainModel>(
        oneDomainClient,
        ClientDomainModel(
            id = 2L,
            name = "Juan",
            lastName = "López",
            phone = "3216549870",
            numberIdentifier = 50))

    val oneEntityClient = ClienteEntity(
        id = 1L,
        nombre = "José",
        apellido = "López",
        telefono = "3216549870",
        identificadorCliente = 30
    )

    val listEntityClient = listOf<ClienteEntity>(
        oneEntityClient,
        ClienteEntity(
            id = 2L,
            nombre = "Juan",
            apellido = "López",
            telefono = "3216549870",
            identificadorCliente = 50))

    /*Variables a utilizar en el testing del repositorio de proveedores y viewModels*/

    val oneDomainSupplier = SupplierDomainModel(
        id = 1L,
        company = "Kenda",
        contactName = "Alberto del Dio",
        phone = "3216549870",
        email = "",
        address = ""
    )

    val listDomainSupplier = listOf<SupplierDomainModel>(
        oneDomainSupplier,
        SupplierDomainModel(
            id = 2L,
            company = "Kenda",
            contactName = "Alberto del Dio",
            phone = "3216549870",
            email = "",
            address = ""))

    val oneEntitySupplier = ProveedorEntity(
        id = 1L,
        nombreEmpresa = "Kenda",
        nombreContacto = "Alberto del Dio",
        telefono = "3216549870",
        correo = "",
        direccion = ""
    )

    val listEntitySupplier = listOf<ProveedorEntity>(
        oneEntitySupplier,
        ProveedorEntity(
            id = 2L,
            nombreEmpresa = "Kenda",
            nombreContacto = "Alberto del Dio",
            telefono = "3216549870",
            correo = "",
            direccion = ""))
}