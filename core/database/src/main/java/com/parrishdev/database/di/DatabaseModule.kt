package com.parrishdev.database.di

import android.content.Context
import androidx.room.Room
import com.parrishdev.database.F1Database
import com.parrishdev.driver.db.DriverDao
import com.parrishdev.race.db.RaceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideF1Database(
        @ApplicationContext context: Context
    ): F1Database {
        return Room.databaseBuilder(
            context,
            F1Database::class.java,
            "f1_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideRaceDao(database: F1Database): RaceDao {
        return database.raceDao()
    }

    @Provides
    fun provideDriverDao(database: F1Database): DriverDao {
        return database.driverDao()
    }
}
