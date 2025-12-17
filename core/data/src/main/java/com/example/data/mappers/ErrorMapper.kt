package com.example.data.mappers

import com.apollographql.apollo3.exception.ApolloException
import com.example.domain.result.DataError
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Extension function to map technical exceptions into Domain-specific errors.
 */
fun Throwable.toDataError(): DataError {
    return when (this) {
        // 1. Connectivity Errors
        is SocketTimeoutException -> DataError.Network // Specific timeout
        is IOException -> DataError.Network // No connection / DNS error / Unknown host

        // 2. HTTP Errors (Retrofit)
        is HttpException -> {
            when (this.code()) {
                in 400..499 -> DataError.Unknown("Client Error: ${this.code()}") // e.g., 404 Not Found, 403 Forbidden
                in 500..599 -> DataError.Server // Server side errors
                else -> DataError.Unknown("Http Error: ${this.code()}")
            }
        }

        // 3. Serialization Errors (Malformed JSON or DataType mismatch)
        is SerializationException -> DataError.Serialization

        // 4. Apollo (GraphQL) Errors
        // Apollo throws ApolloException for network or parsing issues
        is ApolloException -> DataError.Network

        // 5. Fallback for any other exception
        else -> DataError.Unknown(this.message ?: "Unknown error")
    }
}
