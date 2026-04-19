package com.soundflow.app.data.mapper

import com.soundflow.app.data.local.database.entity.TrackEntity
import com.soundflow.app.data.remote.dto.JamendTrackDto
import com.soundflow.app.domain.model.Album
import com.soundflow.app.domain.model.Artist
import com.soundflow.app.domain.model.Track
import com.soundflow.app.domain.model.TrackSource

fun JamendTrackDto.toDomain(): Track {
    return Track(
        id = "jamendo_$id",
        title = name,
        artist = Artist(
            id = "jamendo_$artist_id",
            name = artist_name,
            avatarUrl = artist_image
        ),
        album = if (album_id != null) Album(
            id = "jamendo_$album_id",
            title = album_name ?: "",
            artistId = "jamendo_$artist_id",
            artworkUrl = album_image
        ) else null,
        duration = (duration * 1000).toLong(),
        streamUrl = audio,
        artworkUrl = image,
        genre = musicinfo?.genres?.firstOrNull() ?: musicinfo?.tags?.firstOrNull(),
        source = TrackSource.JAMENDO,
        isDownloadable = audiodownload != null
    )
}

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = id,
        title = title,
        artistId = artist.id,
        artistName = artist.name,
        albumId = album?.id,
        albumName = album?.title,
        duration = duration,
        streamUrl = streamUrl,
        artworkUrl = artworkUrl,
        genre = genre,
        source = source,
        localPath = localPath,
        isDownloaded = isDownloadable,
        isFavorite = isFavorite
    )
}

fun TrackEntity.toDomain(): Track {
    return Track(
        id = id,
        title = title,
        artist = Artist(
            id = artistId,
            name = artistName
        ),
        album = if (albumId != null) Album(
            id = albumId,
            title = albumName ?: "",
            artistId = artistId
        ) else null,
        duration = duration,
        streamUrl = streamUrl,
        artworkUrl = artworkUrl,
        genre = genre,
        source = source,
        isDownloadable = isDownloaded,
        isFavorite = isFavorite,
        localPath = localPath
    )
}
