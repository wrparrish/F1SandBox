package com.parrishdev.race.api.di

import com.parrishdev.race.api.RaceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Hilt module that provides [RaceApi] instance.
 * Uses the Jolpica F1 API (successor to deprecated Ergast API).
 */
@Module
@InstallIn(SingletonComponent::class)
object RaceApiModule {

    @Provides
    @Singleton
    fun provideRaceApi(
        @Named("Jolpica") retrofit: Retrofit
    ): RaceApi {
        return retrofit.create(RaceApi::class.java)
    }
}
