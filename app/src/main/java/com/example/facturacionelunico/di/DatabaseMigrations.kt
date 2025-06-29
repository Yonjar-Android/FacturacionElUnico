package com.example.facturacionelunico.di

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

    val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Evitar duplicaciones
            db.execSQL("DROP TRIGGER IF EXISTS actualizar_stock_compra")
            db.execSQL("DROP TRIGGER IF EXISTS actualizar_abono_y_estado_compra")

            // Trigger para stock de productos al hacer una compra
            db.execSQL(
                """
            CREATE TRIGGER actualizar_stock_compra
            AFTER INSERT ON detalle_compra
            BEGIN
                UPDATE producto
                SET stock = stock + NEW.cantidad
                WHERE id = NEW.idProducto;
            END;
        """.trimIndent()
            )

            // Trigger para actualizar abono y estado de compra
            db.execSQL(
                """
            CREATE TRIGGER actualizar_abono_y_estado_compra
            AFTER INSERT ON detalle_abono_compra
            BEGIN
                UPDATE abono_compra
                SET totalPendiente = totalPendiente - NEW.monto
                WHERE id = NEW.idAbonoCompra;

                UPDATE compra
                SET estado = 'COMPLETADO'
                WHERE id IN (
                    SELECT idCompra
                    FROM abono_compra
                    WHERE id = NEW.idAbonoCompra AND totalPendiente <= 0
                );
            END;
        """.trimIndent()
            )
        }
    }


    // Usar esto solo si no usas migraciones, o si es instalación nueva (versión inicial)
    val dbCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            db.execSQL("DROP TRIGGER IF EXISTS actualizar_stock")
            db.execSQL("DROP TRIGGER IF EXISTS actualizar_abono_y_estado_venta")
            db.execSQL("DROP TRIGGER IF EXISTS actualizar_stock_detalle_compra")
            db.execSQL("DROP TRIGGER IF EXISTS actualizar_stock_al_eliminar_detalle_compra")
            db.execSQL("DROP TRIGGER IF EXISTS actualizar_stock_detalle_venta")
            db.execSQL("DROP TRIGGER IF EXISTS actualizar_stock_al_eliminar_detalle_venta")

            db.execSQL(
                """
                CREATE TRIGGER actualizar_stock_al_eliminar_detalle_venta
                AFTER DELETE ON detalle_venta
                BEGIN
                    UPDATE producto
                    SET stock = stock + OLD.cantidad
                    WHERE id = OLD.idProducto;
                END;
            """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TRIGGER actualizar_stock_detalle_venta
                AFTER UPDATE ON detalle_venta
                BEGIN
                UPDATE producto
                SET stock = stock + OLD.cantidad - NEW.cantidad
                WHERE id = NEW.idProducto;
                END;
            """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TRIGGER actualizar_stock_al_eliminar_detalle_compra
                AFTER DELETE ON detalle_compra
                BEGIN
                    UPDATE producto
                    SET stock = stock - OLD.cantidad
                    WHERE id = OLD.idProducto;
                END;
            """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TRIGGER actualizar_stock_detalle_compra
                AFTER UPDATE ON detalle_compra
                BEGIN
                    UPDATE producto
                    SET stock = stock + (NEW.cantidad - OLD.cantidad)
                    WHERE id = NEW.idProducto;
                END;
            """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TRIGGER actualizar_stock
                AFTER INSERT ON detalle_venta
                BEGIN
                    UPDATE producto
                    SET stock = stock - NEW.cantidad
                    WHERE id = NEW.idProducto;
                END;
            """.trimIndent()
            )

            db.execSQL(
                """
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
            """.trimIndent()
            )

            // Evitar duplicaciones
            db.execSQL("DROP TRIGGER IF EXISTS actualizar_stock_compra")
            db.execSQL("DROP TRIGGER IF EXISTS actualizar_abono_y_estado_compra")

            // Trigger para stock de productos al hacer una compra
            db.execSQL(
                """
            CREATE TRIGGER actualizar_stock_compra
            AFTER INSERT ON detalle_compra
            BEGIN
                UPDATE producto
                SET stock = stock + NEW.cantidad
                WHERE id = NEW.idProducto;
            END;
        """.trimIndent()
            )

            // Trigger para actualizar abono y estado de compra
            db.execSQL(
                """
            CREATE TRIGGER actualizar_abono_y_estado_compra
            AFTER INSERT ON detalle_abono_compra
            BEGIN
                UPDATE abono_compra
                SET totalPendiente = totalPendiente - NEW.monto
                WHERE id = NEW.idAbonoCompra;

                UPDATE compra
                SET estado = 'COMPLETADO'
                WHERE id IN (
                    SELECT idCompra
                    FROM abono_compra
                    WHERE id = NEW.idAbonoCompra AND totalPendiente <= 0
                );
            END;
        """.trimIndent()
            )
        }
    }


}