package com.example.domain.result

sealed interface DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>
    data class Error(val error: DataError) : DomainResult<Nothing>
}
