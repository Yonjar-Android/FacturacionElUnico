package com.example.facturacionelunico.domain.models

/* Implementación de result pattern
 Utilizada para que la función de un repositorio devuelva un tipo
 Específico de dato o un texto para avisar al usuario sobre una acción o error*/
sealed class ResultPattern<out T> {
    data class Success<out T>(val data: T) : ResultPattern<T>()

    data class Error(val exception: Throwable? = null, val message: String? = null) :
        ResultPattern<Nothing>()
}