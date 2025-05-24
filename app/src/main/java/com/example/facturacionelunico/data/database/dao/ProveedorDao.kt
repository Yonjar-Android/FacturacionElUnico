package com.example.facturacionelunico.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.ProveedorEntity
import com.example.facturacionelunico.domain.models.SupplierDomainModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ProveedorDao {
    @Insert
    suspend fun insert(proveedor: ProveedorEntity)

    @Update
    suspend fun update(proveedor: ProveedorEntity)

    @Query("SELECT * FROM proveedor")
    fun getAll(): PagingSource<Int, ProveedorEntity>

    @Query("SELECT * FROM proveedor WHERE id = :id")
    fun getSupplierById(id: Long): Flow<ProveedorEntity>

    // Verificar si un proveedor con el mismo nombre de empresa ya existe, excluyendo el proveedor actual
    @Query("SELECT * FROM proveedor WHERE LOWER(nombreEmpresa) = LOWER(:nombre) AND id != :id LIMIT 1")
    suspend fun getSupplierByCompany(nombre: String, id: Long): ProveedorEntity?

    @Query("""
        SELECT proveedor.id as id,
        proveedor.nombreEmpresa as company,
        proveedor.nombreContacto as contactName,
        proveedor.telefono as phone,
        proveedor.correo as email,
        proveedor.direccion as address
        FROM proveedor
        WHERE nombreEmpresa LIKE '%' || :query || '%'
    """)
    fun getSuppliersBySearch(query:String): PagingSource<Int, SupplierDomainModel>
}
