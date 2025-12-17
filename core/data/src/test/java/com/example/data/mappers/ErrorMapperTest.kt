package com.example.data.mappers

import com.apollographql.apollo3.exception.ApolloException
import com.example.domain.result.DataError
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class ErrorMapperTest {

    @Test
    fun `GIVEN SocketTimeoutException WHEN toDataError called THEN returns DataError Network`() {
        val exception = SocketTimeoutException()
        val result = exception.toDataError()
        assertThat(result).isEqualTo(DataError.Network)
    }

    @Test
    fun `GIVEN IOException WHEN toDataError called THEN returns DataError Network`() {
        val exception = IOException()
        val result = exception.toDataError()
        assertThat(result).isEqualTo(DataError.Network)
    }

    @Test
    fun `GIVEN ApolloException WHEN toDataError called THEN returns DataError Network`() {
        val exception = ApolloException("GraphQL error")
        val result = exception.toDataError()
        assertThat(result).isEqualTo(DataError.Network)
    }

    @Test
    fun `GIVEN SerializationException WHEN toDataError called THEN returns DataError Serialization`() {
        val exception = SerializationException("Bad JSON")
        val result = exception.toDataError()
        assertThat(result).isEqualTo(DataError.Serialization)
    }

    @Test
    fun `GIVEN HttpException 404 WHEN toDataError called THEN returns DataError Unknown (Client Error)`() {
        // Create a real Retrofit HttpException
        val errorResponse = Response.error<Any>(404, "".toResponseBody(null))
        val exception = HttpException(errorResponse)

        val result = exception.toDataError()

        assertThat(result).isInstanceOf(DataError.Unknown::class.java)
        assertThat((result as DataError.Unknown).message).contains("404")
    }

    @Test
    fun `GIVEN HttpException 500 WHEN toDataError called THEN returns DataError Server`() {
        val errorResponse = Response.error<Any>(500, "".toResponseBody(null))
        val exception = HttpException(errorResponse)

        val result = exception.toDataError()

        assertThat(result).isEqualTo(DataError.Server)
    }

    @Test
    fun `GIVEN Generic Exception WHEN toDataError called THEN returns DataError Unknown`() {
        val exception = Exception("Something bad happened")
        val result = exception.toDataError()

        assertThat(result).isInstanceOf(DataError.Unknown::class.java)
        assertThat((result as DataError.Unknown).message).isEqualTo("Something bad happened")
    }
}
