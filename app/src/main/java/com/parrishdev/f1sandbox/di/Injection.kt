package com.parrishdev.f1sandbox.di

import com.parrishdev.data.F1DriversApi
import com.parrishdev.data.F1DriversApiImpl
import com.parrishdev.data.MeetingsApi
import com.parrishdev.data.MeetingsApiImpl
import com.parrishdev.network.F1Endpoint
import com.squareup.moshi.Moshi
import dagger.Binds
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
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindF1DriversApi(impl: F1DriversApiImpl): F1DriversApi

    @Binds
    abstract fun bindMeetingsApi(impl: MeetingsApiImpl): MeetingsApi
}


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://api.openf1.org/v1/"

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
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideF1Endpoint(retrofit: Retrofit): F1Endpoint {
        return retrofit.create(F1Endpoint::class.java)
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