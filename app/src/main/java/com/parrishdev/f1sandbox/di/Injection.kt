package com.parrishdev.f1sandbox.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Network infrastructure module.
 *
 * Provides shared network components used by domain-specific API modules:
 * - driver/lib-api-driver uses @Named("OpenF1") Retrofit
 * - race/lib-api-race uses @Named("Jolpica") Retrofit
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val OPENF1_BASE_URL = "https://api.openf1.org/v1/"
    private const val JOLPICA_BASE_URL = "https://api.jolpi.ca/ergast/f1/"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("OpenF1")
    fun provideOpenF1Retrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(OPENF1_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    @Named("Jolpica")
    fun provideJolpicaRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(JOLPICA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AsyncDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @AsyncDispatcher
    @Provides
    fun providesAsyncDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
