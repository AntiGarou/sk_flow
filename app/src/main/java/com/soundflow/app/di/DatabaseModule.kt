package com.soundflow.app.di

import android.content.Context
import androidx.room.Room
import com.soundflow.app.data.local.database.Converters
import com.soundflow.app.data.local.database.SoundFlowDatabase
import com.soundflow.app.data.local.database.dao.PlaylistDao
import com.soundflow.app.data.local.database.dao.SearchHistoryDao
import com.soundflow.app.data.local.database.dao.TrackDao
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SoundFlowDatabase {
        return Room.databaseBuilder(
            context,
            SoundFlowDatabase::class.java,
            "soundflow_database"
        )
            .addTypeConverter(Converters())
            .build()
    }

    @Provides
    fun provideTrackDao(database: SoundFlowDatabase): TrackDao = database.trackDao()

    @Provides
    fun providePlaylistDao(database: SoundFlowDatabase): PlaylistDao = database.playlistDao()

    @Provides
    fun provideSearchHistoryDao(database: SoundFlowDatabase): SearchHistoryDao = database.searchHistoryDao()
}
