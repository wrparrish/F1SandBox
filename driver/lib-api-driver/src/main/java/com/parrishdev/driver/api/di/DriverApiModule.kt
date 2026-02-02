package com.parrishdev.driver.api.di

import com.parrishdev.driver.api.DriverApi
import com.parrishdev.driver.api.StandingsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Hilt module that provides driver-related API instances.
 *
 * - [DriverApi] uses OpenF1 for driver data (headshots, team colors)
 * - [StandingsApi] uses Jolpica for championship standings (points, position)
 */
@Module
@InstallIn(SingletonComponent::class)
object DriverApiModule {

    @Provides
    @Singleton
    fun provideDriverApi(
        @Named("OpenF1") retrofit: Retrofit
    ): DriverApi {
        return retrofit.create(DriverApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStandingsApi(
        @Named("Jolpica") retrofit: Retrofit
    ): StandingsApi {
        return retrofit.create(StandingsApi::class.java)
    }
}
