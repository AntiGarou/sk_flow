package com.soundflow.app.data.local.database

import androidx.room.TypeConverter
import com.soundflow.app.domain.model.TrackSource

class Converters {
    @TypeConverter
    fun fromTrackSource(source: TrackSource): String = source.name

    @TypeConverter
    fun toTrackSource(value: String): TrackSource = TrackSource.valueOf(value)
}
