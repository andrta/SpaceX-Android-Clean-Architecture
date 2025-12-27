package com.example.data.mappers

import com.example.domain.exception.StorageException
import com.example.domain.result.DataError
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

fun Throwable.toDataError(): DataError {
    return when (this) {
        // --- Gestione RETROFIT / Network ---
        is IOException -> {
            when (this) {
                is SocketTimeoutException -> DataError.Network.RequestTimeout
                is UnknownHostException -> DataError.Network.NoInternet
                else -> DataError.Network.Unknown(this.localizedMessage ?: "Unknown IO Error")
            }
        }

        is HttpException -> {
            when (this.code()) {
                408 -> DataError.Network.RequestTimeout
                429 -> DataError.Network.TooManyRequests
                413 -> DataError.Network.PayloadTooLarge(0) // Valore dummy o estratto header
                in 500..599 -> DataError.Network.Server
                else -> DataError.Network.Unknown("HTTP Error ${this.code()}")
            }
        }

        // --- Gestione SERIALIZZAZIONE (Kotlinx Serialization / GSON) ---
        is SerializationException, is IllegalArgumentException -> {
            // Spesso IllegalArgumentException capita nel parsing JSON
            DataError.Network.Serialization
        }

        // --- Gestione DATABASE (Room / SQLite) ---
        is StorageException -> {
            DataError.Local.DiskRead // O DiskWrite in base al contesto, semplifichiamo
        }

        // --- Default ---
        else -> DataError.Network.Unknown(this.localizedMessage ?: "Unknown Error")
    }
}
