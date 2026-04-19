package com.soundflow.app.data.remote.api

import com.soundflow.app.data.remote.dto.SoundCloudSearchResponse
import com.soundflow.app.data.remote.dto.SoundCloudStreamResponse
import com.soundflow.app.data.remote.dto.SoundCloudTrackDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface SoundCloudApi {

    @GET("search/tracks")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): SoundCloudSearchResponse

    @GET("tracks")
    suspend fun getTrendingTracks(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): List<SoundCloudTrackDto>

    @GET("tracks/{id}")
    suspend fun getTrack(
        @Path("id") id: Long
    ): SoundCloudTrackDto

    @GET("users/{id}/tracks")
    suspend fun getUserTracks(
        @Path("id") userId: Long,
        @Query("limit") limit: Int = 20
    ): List<SoundCloudTrackDto>

    @GET
    suspend fun getStreamUrl(@Url url: String): SoundCloudStreamResponse
}
