package com.example.domain.result

sealed interface DataError {
    sealed interface Network : DataError {
        data object RequestTimeout : Network
        data object NoInternet : Network
        data object TooManyRequests : Network
        data object Server : Network
        data object Serialization : Network
        data class PayloadTooLarge(val limit: Long) : Network
        data class Unknown(val message: String) : Network
    }

    sealed interface Local : DataError {
        data object DiskRead : Local
    }
}
