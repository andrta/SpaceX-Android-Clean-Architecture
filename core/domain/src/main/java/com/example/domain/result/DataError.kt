package com.example.domain.result

sealed interface DataError {
    data object Network : DataError         // No connection or Timeout
    data object Server : DataError          // 500, 404, etc.
    data object Serialization : DataError   // JSON malformed
    data class Unknown(val message: String) : DataError // Unknown error
}
