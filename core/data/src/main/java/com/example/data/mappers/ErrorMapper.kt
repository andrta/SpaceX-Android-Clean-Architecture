package com.example.data.mappers

import com.apollographql.apollo3.exception.ApolloException
import com.example.domain.exception.StorageException
import com.example.domain.result.DataError
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toDataError(): DataError {
    return when (this) {
        is SocketTimeoutException -> DataError.Network.RequestTimeout
        is UnknownHostException -> DataError.Network.NoInternet
        is ApolloException -> DataError.Network.Server
        is HttpException -> {
            when (this.code()) {
                408 -> DataError.Network.RequestTimeout
                413 -> DataError.Network.PayloadTooLarge(0)
                429 -> DataError.Network.TooManyRequests
                in 500..599 -> DataError.Network.Server
                else -> DataError.Network.Unknown("HTTP Error ${this.code()}")
            }
        }
        is IOException -> DataError.Network.NoInternet
        is SerializationException, is IllegalArgumentException -> DataError.Network.Serialization
        is StorageException -> DataError.Local.DiskRead
        else -> DataError.Network.Unknown(this.localizedMessage ?: "Unknown Error")
    }
}
