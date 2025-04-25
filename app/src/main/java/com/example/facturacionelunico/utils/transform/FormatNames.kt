package com.example.facturacionelunico.utils.transform

object FormatNames {
    fun firstLetterUpperCase(name: String): String {
        return name.trim().lowercase().replaceFirstChar { it.uppercase() }

    }
}