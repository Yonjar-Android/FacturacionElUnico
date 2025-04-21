package com.example.facturacionelunico.utils.validations

object ValidationFunctions {
    fun isValidInt(stock: String?): Boolean {
        return try {
            val stockValue = stock?.toInt()
            stockValue != null && stockValue >= 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isValidDouble(input: String?): Boolean {
        return try {
            input?.toDouble() != null
        } catch (e: NumberFormatException) {
            false
        }
    }
}