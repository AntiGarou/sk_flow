package com.soundflow.app.di

import com.soundflow.app.BuildConfig
import com.soundflow.app.data.remote.api.JamendoApi
import com.soundflow.app.data.remote.api.SoundCloudApi
import com.soundflow.app.data.remote.soundcloud.SoundCloudAuthInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                }
            )
            .build()
    }

    @Provides
    @Singleton
    @Named("jamendo")
    fun provideJamendoRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.jamendo.com/v3.0/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideJamendoApi(@Named("jamendo") retrofit: Retrofit): JamendoApi {
        return retrofit.create(JamendoApi::class.java)
    }

    @Provides
    @Singleton
    @Named("soundcloud")
    fun provideSoundCloudOkHttpClient(
        okHttpClient: OkHttpClient,
        authInterceptor: SoundCloudAuthInterceptor
    ): OkHttpClient {
        return okHttpClient.newBuilder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("soundcloud")
    fun provideSoundCloudRetrofit(
        @Named("soundcloud") okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api-v2.soundcloud.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideSoundCloudApi(@Named("soundcloud") retrofit: Retrofit): SoundCloudApi {
        return retrofit.create(SoundCloudApi::class.java)
    }

    @Provides
    @Singleton
    @Named("jamendoClientId")
    fun provideJamendoClientId(): String = BuildConfig.JAMENDO_CLIENT_ID
}
