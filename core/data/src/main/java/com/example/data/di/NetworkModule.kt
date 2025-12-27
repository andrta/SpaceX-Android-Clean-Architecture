package com.example.data.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.example.data.services.SpaceXApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RestBaseUrl

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class GraphQlBaseUrl

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val REST_BASE_URL = "https://api.spacexdata.com/v5/"
    private const val GRAPHQL_BASE_URL = "https://spacex-production.up.railway.app/"

    @Provides
    @Singleton
    @RestBaseUrl
    fun provideBaseUrl(): String = REST_BASE_URL

    @Provides
    @Singleton
    @GraphQlBaseUrl
    fun provideGraphBaseUrl(): String = GRAPHQL_BASE_URL

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true // Ignores JSON extra fields to avoid crash
            coerceInputValues = true // Better null/default management
            encodeDefaults = true
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(provideBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideSpaceXApiService(retrofit: Retrofit): SpaceXApiService {
        return retrofit.create(SpaceXApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApolloClient(okHttpClient: OkHttpClient): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(provideGraphBaseUrl())
            .okHttpClient(okHttpClient)
            .build()
    }
}
