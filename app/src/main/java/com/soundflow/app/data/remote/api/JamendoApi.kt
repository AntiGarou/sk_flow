package com.soundflow.app.data.remote.api

import com.soundflow.app.data.remote.dto.JamendoArtistsResponse
import com.soundflow.app.data.remote.dto.JamendoTracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JamendoApi {
    @GET("tracks/")
    suspend fun getTracks(
        @Query("client_id") clientId: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("order") order: String = "popularity_total",
        @Query("include") include: String = "musicinfo",
        @Query("audioformat") audioFormat: String = "mp32"
    ): JamendoTracksResponse

    @GET("tracks/")
    suspend fun searchTracks(
        @Query("client_id") clientId: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("namesearch") query: String,
        @Query("include") include: String = "musicinfo",
        @Query("audioformat") audioFormat: String = "mp32"
    ): JamendoTracksResponse

    @GET("tracks/")
    suspend fun getTracksByGenre(
        @Query("client_id") clientId: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("tags") genre: String,
        @Query("order") order: String = "popularity_total",
        @Query("include") include: String = "musicinfo",
        @Query("audioformat") audioFormat: String = "mp32"
    ): JamendoTracksResponse

    @GET("artists/")
    suspend fun searchArtists(
        @Query("client_id") clientId: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("namesearch") query: String
    ): JamendoArtistsResponse
}
