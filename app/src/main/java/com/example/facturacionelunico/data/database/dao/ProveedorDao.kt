package com.example.facturacionelunico.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.facturacionelunico.data.database.entities.ProveedorEntity
import com.example.facturacionelunico.domain.models.supplier.DetailedSupplierLocalModel
import com.example.facturacionelunico.domain.models.supplier.SupplierDomainModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ProveedorDao {
    @Insert
    suspend fun insert(proveedor: ProveedorEntity)

    @Update
    suspend fun update(proveedor: ProveedorEntity)

    @Query("SELECT * FROM proveedor")
    fun getAll(): PagingSource<Int, ProveedorEntity>

    @Query("""
        SELECT 
        p.id as id,
        p.nombreEmpresa as company,
        p.nombreContacto as contactName,
        p.telefono as phone,
        p.correo as email,
        p.direccion as address,
        IFNULL(SUM(a.totalPendiente), 0.0) AS deptTotal
        FROM proveedor p
        LEFT JOIN compra c ON p.id = c.idProveedor
        LEFT JOIN abono_compra a ON c.id = a.idCompra
        GROUP BY p.id
        ORDER BY deptTotal DESC
    """)
    fun getSuppliersWithDebt(): PagingSource<Int, DetailedSupplierLocalModel>

    @Query("""
        SELECT 
        p.id as id,
        p.nombreEmpresa as company,
        p.nombreContacto as contactName,
        p.telefono as phone,
        p.correo as email,
        p.direccion as address,
        IFNULL(SUM(a.totalPendiente), 0.0) AS deptTotal
        FROM proveedor p
        LEFT JOIN compra c ON p.id = c.idProveedor
        LEFT JOIN abono_compra a ON c.id = a.idCompra
        WHERE p.id = :id
        GROUP BY p.id
        ORDER BY deptTotal DESC
    """)
    fun getSupplierById(id: Long): Flow<DetailedSupplierLocalModel>

    // Verificar si un proveedor con el mismo nombre de empresa ya existe, excluyendo el proveedor actual
    @Query("SELECT * FROM proveedor WHERE LOWER(nombreEmpresa) = LOWER(:nombre) AND id != :id LIMIT 1")
    suspend fun getSupplierByCompany(nombre: String, id: Long): ProveedorEntity?

    @Query("""
        SELECT proveedor.id as id,
        proveedor.nombreEmpresa as company,
        proveedor.nombreContacto as contactName,
        proveedor.telefono as phone,
        proveedor.correo as email,
        proveedor.direccion as address,
        IFNULL(SUM(a.totalPendiente), 0.0) as deptTotal
        FROM proveedor
        LEFT JOIN compra c ON proveedor.id = c.idProveedor
        LEFT JOIN abono_compra a ON c.id = a.idCompra
        WHERE nombreEmpresa LIKE '%' || :query || '%'
        GROUP BY proveedor.id
        ORDER BY deptTotal DESC
    """)
    fun getSuppliersBySearch(query:String): PagingSource<Int, DetailedSupplierLocalModel>
}
