package com.parrishdev.race.store.impl.di

import com.parrishdev.race.store.RaceStore
import com.parrishdev.race.store.impl.RaceStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds [RaceStore] interface to [RaceStoreImpl].
 *
 * This follows the dependency inversion principle:
 * - Features depend on [RaceStore] interface
 * - Implementation ([RaceStoreImpl]) is bound here
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RaceStoreModule {

    @Binds
    @Singleton
    abstract fun bindRaceStore(impl: RaceStoreImpl): RaceStore
}
