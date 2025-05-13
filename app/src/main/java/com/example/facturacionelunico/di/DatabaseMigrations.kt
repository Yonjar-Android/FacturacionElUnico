package com.example.facturacionelunico.di

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Evitar duplicación: eliminar primero
            db.execSQL("DROP TRIGGER IF EXISTS actualizar_stock")
            db.execSQL("DROP TRIGGER IF EXISTS actualizar_abono_y_estado_venta")

            // Crear trigger de stock
            db.execSQL("""
                CREATE TRIGGER actualizar_stock
                AFTER INSERT ON detalle_venta
                BEGIN
                    UPDATE producto
                    SET stock = stock - NEW.cantidad
                    WHERE id = NEW.idProducto;
                END;
            """.trimIndent())

            // Crear trigger de abono y estado
            db.execSQL("""
                CREATE TRIGGER actualizar_abono_y_estado_venta
                AFTER INSERT ON detalle_abono_venta
                BEGIN
                    UPDATE abono_venta
                    SET totalPendiente = totalPendiente - NEW.monto
                    WHERE id = NEW.idAbonoVenta;

                    UPDATE venta
                    SET estado = 'COMPLETADO'
                    WHERE id IN (
                        SELECT idVenta
                        FROM abono_venta
                        WHERE id = NEW.idAbonoVenta AND totalPendiente <= 0
                    );
                END;
            """.trimIndent())
        }
    }

    // Usar esto solo si no usas migraciones, o si es instalación nueva (versión inicial)
    val dbCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            db.execSQL("DROP TRIGGER IF EXISTS actualizar_stock")
            db.execSQL("DROP TRIGGER IF EXISTS actualizar_abono_y_estado_venta")

            db.execSQL("""
                CREATE TRIGGER actualizar_stock
                AFTER INSERT ON detalle_venta
                BEGIN
                    UPDATE producto
                    SET stock = stock - NEW.cantidad
                    WHERE id = NEW.idProducto;
                END;
            """.trimIndent())

            db.execSQL("""
                CREATE TRIGGER actualizar_abono_y_estado_venta
                AFTER INSERT ON detalle_abono_venta
                BEGIN
                    UPDATE abono_venta
                    SET totalPendiente = totalPendiente - NEW.monto
                    WHERE id = NEW.idAbonoVenta;

                    UPDATE venta
                    SET estado = 'COMPLETADO'
                    WHERE id IN (
                        SELECT idVenta
                        FROM abono_venta
                        WHERE id = NEW.idAbonoVenta AND totalPendiente <= 0
                    );
                END;
            """.trimIndent())
        }
    }


}