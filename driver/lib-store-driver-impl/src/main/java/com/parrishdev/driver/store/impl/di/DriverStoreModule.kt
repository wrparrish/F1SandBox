package com.parrishdev.driver.store.impl.di

import com.parrishdev.driver.store.DriverStore
import com.parrishdev.driver.store.impl.DriverStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds [DriverStore] interface to [DriverStoreImpl].
 *
 * This follows the dependency inversion principle:
 * - Features depend on [DriverStore] interface
 * - Implementation ([DriverStoreImpl]) is bound here
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DriverStoreModule {

    @Binds
    @Singleton
    abstract fun bindDriverStore(impl: DriverStoreImpl): DriverStore
}
