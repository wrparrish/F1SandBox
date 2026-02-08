package com.parrishdev.settings.store.impl.di

import com.parrishdev.settings.store.SettingsStore
import com.parrishdev.settings.store.impl.SettingsStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds [SettingsStore] interface to [SettingsStoreImpl].
 *
 * This follows the dependency inversion principle:
 * - Features depend on [SettingsStore] interface
 * - Implementation ([SettingsStoreImpl]) is bound here
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsStoreModule {

    @Binds
    @Singleton
    abstract fun bindSettingsStore(impl: SettingsStoreImpl): SettingsStore
}
