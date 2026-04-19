package com.soundflow.app.di

import com.soundflow.app.data.repository.DownloadRepositoryImpl
import com.soundflow.app.data.repository.PlaylistRepositoryImpl
import com.soundflow.app.data.repository.TrackRepositoryImpl
import com.soundflow.app.domain.repository.DownloadRepository
import com.soundflow.app.domain.repository.PlaylistRepository
import com.soundflow.app.domain.repository.TrackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTrackRepository(impl: TrackRepositoryImpl): TrackRepository

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(impl: PlaylistRepositoryImpl): PlaylistRepository

    @Binds
    @Singleton
    abstract fun bindDownloadRepository(impl: DownloadRepositoryImpl): DownloadRepository
}
