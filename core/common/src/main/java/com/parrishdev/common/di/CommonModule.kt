package com.parrishdev.common.di

import android.util.Log
import com.parrishdev.common.udf.DefaultDispatcherProvider
import com.parrishdev.common.udf.DispatcherProvider
import com.parrishdev.common.udf.ViewModelBundle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    @Singleton
    fun provideCoroutineExceptionHandler(): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            // Log unhandled exceptions from coroutines
            Log.e("UDF", "Unhandled coroutine exception", throwable)
        }
    }

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }

    @Provides
    @Singleton
    fun provideViewModelBundle(
        coroutineExceptionHandler: CoroutineExceptionHandler,
        dispatcherProvider: DispatcherProvider
    ): ViewModelBundle {
        return ViewModelBundle(coroutineExceptionHandler, dispatcherProvider)
    }
}
