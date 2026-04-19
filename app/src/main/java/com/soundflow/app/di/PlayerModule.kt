package com.soundflow.app.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.exoplayer.ExoPlayer
import com.soundflow.app.data.repository.PlayerRepositoryImpl
import com.soundflow.app.domain.repository.PlayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(
                AudioAttributes.DEFAULT,
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    @Provides
    @Singleton
    fun providePlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository = impl
}
