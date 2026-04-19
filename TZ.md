# SoundFlow - Technical Specification

## Project Overview

**SoundFlow** is a free Android music streaming application - a Spotify clone that aggregates music from free APIs (SoundCloud, Jamendo, Free Music Archive, and others). The app provides full music discovery, streaming, playlist management, and offline capabilities.

**Target Repository:** https://github.com/AntiGarou/kimi_flow  
**Target AI Model:** Kimi K2.5  
**Platform:** Android (Kotlin + Jetpack Compose)  
**Minimum SDK:** API 26 (Android 8.0)  
**Target SDK:** API 34 (Android 14)

---

## Table of Contents

1. [Architecture](#1-architecture)
2. [Tech Stack](#2-tech-stack)
3. [Features Specification](#3-features-specification)
4. [API Integration](#4-api-integration)
5. [UI/UX Design](#5-uiux-design)
6. [Data Models](#6-data-models)
7. [Project Structure](#7-project-structure)
8. [Implementation Phases](#8-implementation-phases)
9. [Testing Strategy](#9-testing-strategy)
10. [Build & Deployment](#10-build--deployment)

---

## 1. Architecture

### 1.1 Architectural Pattern
**MVVM + Clean Architecture** with Repository Pattern

```
Presentation Layer (UI/ViewModels)
    ↓
Domain Layer (Use Cases/Models)
    ↓
Data Layer (Repositories/DataSources)
    ↓
External APIs / Local Database
```

### 1.2 Key Components

| Layer | Components |
|-------|-----------|
| **Presentation** | Jetpack Compose UI, ViewModels, StateFlow |
| **Domain** | Use Cases, Domain Models, Repository Interfaces |
| **Data** | Repository Implementations, API Services, Local Database (Room) |

### 1.3 Dependency Injection
Use **Hilt** for dependency injection throughout the application.

---

## 2. Tech Stack

### 2.1 Core Technologies

| Category | Technology |
|----------|------------|
| Language | Kotlin 1.9+ |
| UI Framework | Jetpack Compose (Material 3) |
| Architecture | MVVM + Clean Architecture |
| DI Framework | Hilt |
| Networking | Retrofit 2 + OkHttp 4 |
| JSON Parsing | Kotlinx Serialization |
| Image Loading | Coil |
| Local Database | Room |
| Async Operations | Kotlin Coroutines + Flow |
| Audio Playback | ExoPlayer |
| Background Work | WorkManager |
| Caching | OkHttp Cache + Room |

### 2.2 Gradle Dependencies

```kotlin
// Android Core
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("androidx.activity:activity-compose:1.8.2")

// Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-graphics")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.material:material-icons-extended")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

// Hilt DI
implementation("com.google.dagger:hilt-android:2.50")
kapt("com.google.dagger:hilt-compiler:2.50")

// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

// Image Loading
implementation("io.coil-kt:coil-compose:2.5.0")

// Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")

// Media Playback
implementation("androidx.media3:media3-exoplayer:1.2.1")
implementation("androidx.media3:media3-session:1.2.1")
implementation("androidx.media3:media3-ui:1.2.1")

// Background Work
implementation("androidx.work:work-runtime-ktx:2.9.0")

// DataStore Preferences
implementation("androidx.datastore:datastore-preferences:1.0.0")

// Paging
implementation("androidx.paging:paging-runtime-ktx:3.2.1")
implementation("androidx.paging:paging-compose:3.2.1")

// Testing
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("io.mockk:mockk:1.13.9")
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
```

---

## 3. Features Specification

### 3.1 Core Features

#### F1: User Authentication (Optional/Anonymous)
- Anonymous authentication using device ID
- Optional: Google Sign-In integration
- User preferences stored locally

#### F2: Music Discovery
- **Home Screen**: Personalized recommendations, trending tracks, new releases
- **Search**: Full-text search across tracks, artists, albums, playlists
- **Browse by Genre**: Categories (Electronic, Rock, Pop, Hip-Hop, Jazz, Classical, etc.)
- **Trending**: Popular tracks from all sources

#### F3: Music Player
- **Full-screen Player**: Album art, track info, progress bar
- **Controls**: Play/Pause, Next/Previous, Seek, Shuffle, Repeat
- **Queue Management**: View and reorder upcoming tracks
- **Background Playback**: Continue playing when app is minimized
- **Media Session**: Lock screen controls, notification player

#### F4: Playlist Management
- **Create/Edit/Delete Playlists**
- **Add/Remove Tracks**
- **Favorites**: Like/unlike tracks
- **Recently Played**: Auto-generated history

#### F5: Offline Support
- **Download Tracks**: Save for offline listening
- **Download Management**: View storage usage, delete downloads
- **Offline Mode**: Automatic switch when no connection

#### F6: Library
- **My Library**: Playlists, Downloads, Favorites, Recently Played
- **Artists**: Followed artists with their tracks
- **Albums**: Saved albums

### 3.2 Additional Features

#### F7: Audio Quality Settings
- Stream quality selection (Low: 64kbps, Normal: 128kbps, High: 320kbps)
- Download quality selection

#### F8: Crossfade & Gapless Playback
- Seamless transitions between tracks

#### F9: Sleep Timer
- Auto-stop playback after set time

#### F10: Equalizer
- Basic EQ presets (Bass Boost, Treble Boost, etc.)

---

## 4. API Integration

### 4.1 Primary APIs

#### SoundCloud API (Unofficial/Proxy)
```kotlin
Base URL: https://api-v2.soundcloud.com/
Endpoints:
- GET /search/tracks?q={query}&limit={limit}
- GET /tracks/{id}
- GET /users/{id}/tracks
- GET /resolve?url={url}
```

#### Jamendo API (Official - Free)
```kotlin
Base URL: https://api.jamendo.com/v3.0/
Client ID: [REQUIRED - Get from https://devportal.jamendo.com/]
Endpoints:
- GET /tracks/?client_id={id}&format=json&limit={limit}
- GET /tracks/?client_id={id}&search={query}
- GET /artists/?client_id={id}&namesearch={name}
- GET /albums/?client_id={id}&artist_id={id}
```

#### Free Music Archive API
```kotlin
Base URL: https://freemusicarchive.org/api/
API Key: [REQUIRED]
Endpoints:
- GET /get/tracks.json?api_key={key}
- GET /get/curators.json?api_key={key}
```

#### MusicBrainz API (Metadata)
```kotlin
Base URL: https://musicbrainz.org/ws/2/
Endpoints:
- GET /recording/?query={search}
- GET /artist/?query={search}
```

### 4.2 API Client Implementation

Create a unified `MusicApiService` that aggregates results from multiple sources:

```kotlin
interface MusicApiService {
    suspend fun searchTracks(query: String, limit: Int = 20): List<Track>
    suspend fun getTrendingTracks(limit: Int = 20): List<Track>
    suspend fun getTrackById(id: String): Track?
    suspend fun getArtistTracks(artistId: String): List<Track>
    suspend fun getTracksByGenre(genre: String, limit: Int = 20): List<Track>
}
```

### 4.3 API Response Models

```kotlin
@Serializable
data class Track(
    val id: String,
    val title: String,
    val artist: Artist,
    val album: Album?,
    val duration: Long, // milliseconds
    val streamUrl: String?,
    val artworkUrl: String?,
    val genre: String?,
    val source: TrackSource, // SOUNDCLOUD, JAMENDO, FMA
    val isDownloadable: Boolean = false
)

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val bio: String? = null
)

@Serializable
data class Album(
    val id: String,
    val title: String,
    val artistId: String,
    val artworkUrl: String?,
    val year: Int? = null,
    val trackCount: Int = 0
)

@Serializable
data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val artworkUrl: String?,
    val tracks: List<Track> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDownloaded: Boolean = false
)

enum class TrackSource {
    SOUNDCLOUD,
    JAMENDO,
    FREE_MUSIC_ARCHIVE,
    LOCAL
}
```

---

## 5. UI/UX Design

### 5.1 Design System

**Color Palette:**
```kotlin
// Primary Colors
val Primary = Color(0xFF1DB954)        // Spotify Green
val PrimaryDark = Color(0xFF1AA34A)
val PrimaryLight = Color(0xFF1ED760)

// Background Colors
val Background = Color(0xFF121212)     // Dark background
val Surface = Color(0xFF181818)        // Card/Surface
val SurfaceVariant = Color(0xFF282828) // Elevated surface

// Text Colors
val OnBackground = Color(0xFFFFFFFF)
val OnSurface = Color(0xFFB3B3B3)      // Secondary text
val OnSurfaceVariant = Color(0xFF535353) // Disabled/Tertiary
```

**Typography:**
- Use Material 3 Typography with custom font (Circular Std or Inter)
- H1: 28sp Bold (Screen titles)
- H2: 22sp Bold (Section headers)
- H3: 18sp Medium (Card titles)
- Body: 14sp Regular (Content)
- Caption: 12sp Regular (Metadata)

### 5.2 Screen Specifications

#### Screen 1: Splash/Loading
- App logo animation
- Loading indicator
- Navigate to Home after 2 seconds or when data is ready

#### Screen 2: Main Navigation (Bottom Navigation)
```
┌─────────────────────────────────────┐
│  [Content Area]                     │
│                                     │
│                                     │
│                                     │
├─────────────────────────────────────┤
│  🏠      🔍      📚      👤        │
│ Home   Search  Library  Profile     │
└─────────────────────────────────────┘
```

**Navigation Items:**
- Home (🏠)
- Search (🔍)
- Library (📚)
- Profile/Settings (⚙️)

#### Screen 3: Home
```
┌─────────────────────────────────────┐
│  Good Morning/Evening               │
│  [Settings Icon]                    │
├─────────────────────────────────────┤
│  Recently Played                    │
│  [Grid of 6-8 items]                │
├─────────────────────────────────────┤
│  Made For You                       │
│  [Horizontal scroll - Mixes]        │
├─────────────────────────────────────┤
│  Trending Now                       │
│  [Horizontal scroll - Tracks]       │
├─────────────────────────────────────┤
│  New Releases                       │
│  [Horizontal scroll - Albums]       │
├─────────────────────────────────────┤
│  Browse by Genre                    │
│  [Grid of colored cards]            │
└─────────────────────────────────────┘
```

#### Screen 4: Search
```
┌─────────────────────────────────────┐
│  ┌─────────────────────────────┐   │
│  │ 🔍 Search tracks, artists...│   │
│  └─────────────────────────────┘   │
├─────────────────────────────────────┤
│  Browse All                         │
│  [Grid of genre cards]              │
│  • Electronic  • Rock               │
│  • Pop         • Hip-Hop            │
│  • Jazz        • Classical          │
│  • ...                              │
└─────────────────────────────────────┘
```

**Search Results:**
- Tabs: All | Tracks | Artists | Albums | Playlists
- List view with artwork, title, subtitle

#### Screen 5: Library
```
┌─────────────────────────────────────┐
│  Your Library                       │
│  [Sort Icon] [Add Playlist Icon]    │
├─────────────────────────────────────┤
│  Liked Songs          [❤️]          │
│  [Heart icon with gradient]         │
├─────────────────────────────────────┤
│  Recently Played                    │
├─────────────────────────────────────┤
│  Downloads                          │
├─────────────────────────────────────┤
│  Your Playlists                     │
│  [List with artwork, title, count]  │
└─────────────────────────────────────┘
```

#### Screen 6: Now Playing (Full Screen)
```
┌─────────────────────────────────────┐
│  [Down Arrow]  [More Options]       │
├─────────────────────────────────────┤
│                                     │
│     [Large Album Artwork]           │
│     (300dp x 300dp, rounded)        │
│                                     │
├─────────────────────────────────────┤
│  Track Title                        │
│  Artist Name                        │
│  [Heart Icon]                       │
├─────────────────────────────────────┤
│  [========Progress Bar=======]      │
│  1:24              3:45             │
├─────────────────────────────────────┤
│  [⏮️]  [⏯️/▶️]  [⏭️]                │
├─────────────────────────────────────┤
│  [🔀]  [🔁]  [📋 Queue]             │
└─────────────────────────────────────┘
```

**Player Controls:**
- Swipe down to minimize
- Swipe left/right for next/previous
- Tap progress bar to seek
- Long press for playback speed

#### Screen 7: Playlist/Album Detail
```
┌─────────────────────────────────────┐
│  [Back]  [More]                     │
│                                     │
│     [Large Artwork]                 │
│                                     │
│  Playlist Title                     │
│  Description...                     │
│  By Creator • X songs • Y min       │
├─────────────────────────────────────┤
│  [▶️ Shuffle Play] [💾 Download]    │
├─────────────────────────────────────┤
│  [List of tracks]                   │
│  # | Artwork | Title/Artist | ⏱️ | ⋮│
└─────────────────────────────────────┘
```

### 5.3 UI Components

Create reusable components:

```kotlin
// Common Components
@Composable
fun TrackListItem(track: Track, onClick: () -> Unit, onMore: () -> Unit)
@Composable
fun AlbumCard(album: Album, onClick: () -> Unit)
@Composable
fun ArtistCard(artist: Artist, onClick: () -> Unit)
@Composable
fun PlaylistCard(playlist: Playlist, onClick: () -> Unit)
@Composable
fun GenreCard(genre: String, color: Color, onClick: () -> Unit)
@Composable
fun MiniPlayer(viewModel: PlayerViewModel)
@Composable
fun FullScreenPlayer(viewModel: PlayerViewModel)
@Composable
fun ProgressSlider(progress: Float, onSeek: (Float) -> Unit)
@Composable
fun ControlButtons(isPlaying: Boolean, onPlayPause: () -> Unit, ...)
```

---

## 6. Data Models

### 6.1 Entity Classes (Room)

```kotlin
@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artistId: String,
    val artistName: String,
    val albumId: String?,
    val albumName: String?,
    val duration: Long,
    val streamUrl: String?,
    val artworkUrl: String?,
    val genre: String?,
    val source: TrackSource,
    val localPath: String? = null,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val lastPlayedAt: Long? = null,
    val playCount: Int = 0
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String?,
    val artworkUrl: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDownloaded: Boolean = false
)

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistTrackEntity(
    val playlistId: String,
    val trackId: String,
    val position: Int
)

@Entity(tableName = "recently_played")
data class RecentlyPlayedEntity(
    @PrimaryKey val trackId: String,
    val playedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val searchedAt: Long = System.currentTimeMillis()
)
```

### 6.2 Repository Interfaces

```kotlin
interface TrackRepository {
    suspend fun searchTracks(query: String): Flow<List<Track>>
    suspend fun getTrendingTracks(): Flow<List<Track>>
    suspend fun getTrack(id: String): Flow<Track?>
    suspend fun getTracksByGenre(genre: String): Flow<List<Track>>
    suspend fun getDownloadedTracks(): Flow<List<Track>>
    suspend fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun toggleFavorite(trackId: String)
    suspend fun updateLastPlayed(trackId: String)
}

interface PlaylistRepository {
    suspend fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylist(id: String): Flow<Playlist?>
    suspend fun createPlaylist(name: String, description: String?): Playlist
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(id: String)
    suspend fun addTrackToPlaylist(playlistId: String, trackId: String)
    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String)
    suspend fun reorderTracks(playlistId: String, trackIds: List<String>)
}

interface DownloadRepository {
    suspend fun downloadTrack(track: Track): Flow<DownloadProgress>
    suspend fun deleteDownload(trackId: String)
    suspend fun getDownloadedTracks(): Flow<List<Track>>
    suspend fun isDownloaded(trackId: String): Boolean
}

interface PlayerRepository {
    fun getCurrentTrack(): Flow<Track?>
    fun getPlaybackState(): Flow<PlaybackState>
    fun getProgress(): Flow<Pair<Long, Long>> // current, duration
    suspend fun play(track: Track)
    suspend fun playTracks(tracks: List<Track>, startIndex: Int = 0)
    suspend fun pause()
    suspend fun resume()
    suspend fun stop()
    suspend fun next()
    suspend fun previous()
    suspend fun seekTo(position: Long)
    suspend fun setShuffle(enabled: Boolean)
    suspend fun setRepeatMode(mode: RepeatMode)
}
```

---

## 7. Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/soundflow/app/
│   │   │   ├── SoundFlowApplication.kt
│   │   │   ├── di/
│   │   │   │   ├── AppModule.kt
│   │   │   │   ├── DatabaseModule.kt
│   │   │   │   ├── NetworkModule.kt
│   │   │   │   └── RepositoryModule.kt
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── SoundFlowDatabase.kt
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── TrackDao.kt
│   │   │   │   │   │   │   ├── PlaylistDao.kt
│   │   │   │   │   │   │   └── SearchHistoryDao.kt
│   │   │   │   │   │   └── entity/
│   │   │   │   │   │       ├── TrackEntity.kt
│   │   │   │   │   │       ├── PlaylistEntity.kt
│   │   │   │   │   │       └── ...
│   │   │   │   │   └── preferences/
│   │   │   │   │       └── UserPreferences.kt
│   │   │   │   ├── remote/
│   │   │   │   │   ├── api/
│   │   │   │   │   │   ├── SoundCloudApi.kt
│   │   │   │   │   │   ├── JamendoApi.kt
│   │   │   │   │   │   ├── FreeMusicArchiveApi.kt
│   │   │   │   │   │   └── MusicBrainzApi.kt
│   │   │   │   │   ├── dto/
│   │   │   │   │   │   ├── SoundCloudDtos.kt
│   │   │   │   │   │   ├── JamendoDtos.kt
│   │   │   │   │   │   └── ...
│   │   │   │   │   └── interceptor/
│   │   │   │   │       └── AuthInterceptor.kt
│   │   │   │   ├── repository/
│   │   │   │   │   ├── TrackRepositoryImpl.kt
│   │   │   │   │   ├── PlaylistRepositoryImpl.kt
│   │   │   │   │   ├── DownloadRepositoryImpl.kt
│   │   │   │   │   └── PlayerRepositoryImpl.kt
│   │   │   │   └── mapper/
│   │   │   │       ├── TrackMapper.kt
│   │   │   │       └── ...
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Track.kt
│   │   │   │   │   ├── Artist.kt
│   │   │   │   │   ├── Album.kt
│   │   │   │   │   ├── Playlist.kt
│   │   │   │   │   └── PlaybackState.kt
│   │   │   │   ├── repository/
│   │   │   │   │   ├── TrackRepository.kt
│   │   │   │   │   ├── PlaylistRepository.kt
│   │   │   │   │   ├── DownloadRepository.kt
│   │   │   │   │   └── PlayerRepository.kt
│   │   │   │   └── usecase/
│   │   │   │       ├── track/
│   │   │   │       │   ├── SearchTracksUseCase.kt
│   │   │   │       │   ├── GetTrendingTracksUseCase.kt
│   │   │   │       │   ├── ToggleFavoriteUseCase.kt
│   │   │   │       │   └── ...
│   │   │   │       ├── playlist/
│   │   │   │       │   ├── CreatePlaylistUseCase.kt
│   │   │   │       │   ├── AddTrackToPlaylistUseCase.kt
│   │   │   │       │   └── ...
│   │   │   │       └── player/
│   │   │   │           ├── PlayTrackUseCase.kt
│   │   │   │           ├── ControlPlaybackUseCase.kt
│   │   │   │           └── ...
│   │   │   ├── presentation/
│   │   │   │   ├── components/
│   │   │   │   │   ├── TrackListItem.kt
│   │   │   │   │   ├── AlbumCard.kt
│   │   │   │   │   ├── ArtistCard.kt
│   │   │   │   │   ├── PlaylistCard.kt
│   │   │   │   │   ├── GenreCard.kt
│   │   │   │   │   ├── MiniPlayer.kt
│   │   │   │   │   ├── FullScreenPlayer.kt
│   │   │   │   │   ├── ProgressSlider.kt
│   │   │   │   │   └── ControlButtons.kt
│   │   │   │   ├── screens/
│   │   │   │   │   ├── home/
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   └── HomeViewModel.kt
│   │   │   │   │   ├── search/
│   │   │   │   │   │   ├── SearchScreen.kt
│   │   │   │   │   │   └── SearchViewModel.kt
│   │   │   │   │   ├── library/
│   │   │   │   │   │   ├── LibraryScreen.kt
│   │   │   │   │   │   └── LibraryViewModel.kt
│   │   │   │   │   ├── playlist/
│   │   │   │   │   │   ├── PlaylistDetailScreen.kt
│   │   │   │   │   │   └── PlaylistDetailViewModel.kt
│   │   │   │   │   ├── player/
│   │   │   │   │   │   ├── PlayerScreen.kt
│   │   │   │   │   │   └── PlayerViewModel.kt
│   │   │   │   │   └── settings/
│   │   │   │   │       ├── SettingsScreen.kt
│   │   │   │   │       └── SettingsViewModel.kt
│   │   │   │   ├── navigation/
│   │   │   │   │   ├── Screen.kt
│   │   │   │   │   ├── NavigationGraph.kt
│   │   │   │   │   └── BottomNavBar.kt
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Theme.kt
│   │   │   │   │   └── Type.kt
│   │   │   │   └── MainActivity.kt
│   │   │   └── service/
│   │   │       ├── PlaybackService.kt
│   │   │       └── DownloadService.kt
│   │   ├── res/
│   │   │   ├── drawable/
│   │   │   ├── mipmap-xxxhdpi/
│   │   │   ├── values/
│   │   │   └── xml/
│   │   └── AndroidManifest.xml
│   └── test/
└── build.gradle.kts
```

---

## 8. Implementation Phases

### Phase 1: Project Setup & Core Infrastructure (Day 1)
- [x] Create Android project with Kotlin and Jetpack Compose
- [x] Configure Gradle dependencies (Hilt, Retrofit, Room, ExoPlayer, etc.)
- [x] Set up DI modules (AppModule, NetworkModule, DatabaseModule)
- [x] Create base theme (colors, typography)
- [x] Set up navigation structure

### Phase 2: Data Layer (Day 2)
- [x] Create Room database and entities
- [x] Implement API interfaces (SoundCloud, Jamendo)
- [x] Create DTOs and mappers
- [x] Implement Repository classes
- [x] Set up data caching strategy

### Phase 3: Domain Layer (Day 3)
- [x] Create domain models
- [x] Implement Use Cases
- [x] Create repository interfaces
- [x] Set up domain-level error handling

### Phase 4: Audio Playback (Day 4)
- [x] Implement ExoPlayer service
- [x] Create PlayerRepository
- [x] Implement playback controls
- [x] Add media session for background playback
- [ ] Create notification player

### Phase 5: UI Components (Day 5)
- [x] Create reusable UI components
- [x] Implement MiniPlayer
- [x] Create FullScreenPlayer
- [x] Build track list items and cards

### Phase 6: Screens Implementation (Day 6-7)
- [x] Home Screen with recommendations
- [x] Search Screen with filters
- [x] Library Screen
- [x] Playlist Detail Screen
- [x] Settings Screen

### Phase 7: Advanced Features (Day 8)
- [x] Download functionality
- [x] Offline mode
- [x] Playlist management
- [x] Favorites system

### Phase 8: Polish & Testing (Day 9)
- [ ] Add animations and transitions
- [x] Implement error states and loading
- [ ] Add pull-to-refresh
- [ ] Performance optimization
- [ ] Unit tests for ViewModels and Use Cases

### Phase 9: Final Integration (Day 10)
- [ ] End-to-end testing
- [ ] Bug fixes
- [ ] Documentation
- [ ] Build release APK

---

## 9. Testing Strategy

### 9.1 Unit Tests
```kotlin
// ViewModel Tests
@Test
fun `searchTracks emits loading then success`() = runTest { ... }

// Use Case Tests
@Test
fun `toggleFavorite updates repository`() = runTest { ... }

// Repository Tests
@Test
fun `getTrendingTracks returns cached data when offline`() = runTest { ... }
```

### 9.2 UI Tests
```kotlin
@Test
fun playerScreen_displaysTrackInfo() { ... }

@Test
fun searchScreen_performsSearch() { ... }
```

### 9.3 Integration Tests
- API integration tests
- Database migration tests
- Audio playback tests

---

## 10. Build & Deployment

### 10.1 Build Configuration

```kotlin
// app/build.gradle.kts
android {
    namespace = "com.soundflow.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.soundflow.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
```

### 10.2 ProGuard Rules

```proguard
# Retrofit
-keepattributes Signature
-keepattributes Exceptions

# Kotlinx Serialization
-keepattributes *Annotation*
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
```

### 10.3 GitHub Actions CI/CD

Create `.github/workflows/build.yml`:

```yaml
name: Build & Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Run unit tests
      run: ./gradlew test

    - name: Build debug APK
      run: ./gradlew assembleDebug

    - name: Upload APK
      uses: actions/upload-artifact@v4
      with:
        name: debug-apk
        path: app/build/outputs/apk/debug/app-debug.apk
```

---

## 11. API Keys & Configuration

Create `local.properties` (not committed to git):

```properties
# API Keys
JAMENDO_CLIENT_ID=your_jamendo_client_id
FMA_API_KEY=your_fma_api_key

# Optional: SoundCloud (if using official API)
SOUNDCLOUD_CLIENT_ID=your_soundcloud_client_id
```

Create `BuildConfigFields` in `build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }

        buildConfigField("String", "JAMENDO_CLIENT_ID", "\"${localProperties.getProperty("JAMENDO_CLIENT_ID", "")}\"")
        buildConfigField("String", "FMA_API_KEY", "\"${localProperties.getProperty("FMA_API_KEY", "")}\"")
    }
}
```

---

## 12. Key Implementation Details

### 12.1 ExoPlayer Service

```kotlin
class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var exoPlayer: ExoPlayer

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setCallback(MediaSessionCallback())
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
```

### 12.2 Download Manager

```kotlin
class TrackDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: SoundFlowDatabase
) {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    suspend fun downloadTrack(track: Track): Flow<DownloadProgress> = flow {
        val request = DownloadManager.Request(Uri.parse(track.streamUrl))
            .setTitle(track.title)
            .setDescription("${track.artist.name} - Downloading...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MUSIC, "${track.id}.mp3")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)

        val downloadId = downloadManager.enqueue(request)

        // Monitor download progress
        while (true) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytesTotal = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        emit(DownloadProgress.Success)
                        break
                    }
                    DownloadManager.STATUS_FAILED -> {
                        emit(DownloadProgress.Error)
                        break
                    }
                    else -> {
                        val progress = if (bytesTotal > 0) bytesDownloaded.toFloat() / bytesTotal else 0f
                        emit(DownloadProgress.InProgress(progress))
                    }
                }
            }
            cursor.close()
            delay(500)
        }
    }.flowOn(Dispatchers.IO)
}

sealed class DownloadProgress {
    data class InProgress(val progress: Float) : DownloadProgress()
    object Success : DownloadProgress()
    object Error : DownloadProgress()
}
```

### 12.3 Network Bound Resource

```kotlin
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline onFetchFailed: (Throwable) -> Unit = {}
) = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))

        try {
            saveFetchResult(fetch())
            query().map { Resource.Success(it) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.Error(throwable, it) }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}

sealed class Resource<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null) : Resource<T>(data, throwable)
}
```

---

## 13. Error Handling Strategy

### 13.1 Error Types

```kotlin
sealed class AppError : Exception() {
    object NetworkError : AppError()
    object ApiError : AppError()
    object DatabaseError : AppError()
    object PlaybackError : AppError()
    object DownloadError : AppError()
    data class UnknownError(val throwable: Throwable) : AppError()
}
```

### 13.2 UI Error States

```kotlin
@Composable
fun <T> HandleResource(
    resource: Resource<T>,
    onLoading: @Composable () -> Unit = { LoadingIndicator() },
    onError: @Composable (Throwable) -> Unit = { ErrorMessage(it) },
    onSuccess: @Composable (T) -> Unit
) {
    when (resource) {
        is Resource.Loading -> onLoading()
        is Resource.Error -> onError(resource.error ?: Exception("Unknown error"))
        is Resource.Success -> resource.data?.let { onSuccess(it) }
    }
}
```

---

## 14. Performance Guidelines

### 14.1 Image Loading
- Use Coil with crossfade
- Cache images to disk
- Use appropriate image sizes from APIs

### 14.2 List Performance
- Use `LazyColumn` with `key` parameter
- Implement pagination with Paging 3
- Use `rememberSaveable` for scroll state

### 14.3 Database
- Use `@Transaction` for complex operations
- Index frequently queried columns
- Use `Flow` for reactive updates

### 14.4 Memory Management
- Use `remember` and `derivedStateOf` appropriately
- Avoid unnecessary recompositions
- Use `DisposableEffect` for cleanup

---

## 15. Security Considerations

- Store API keys in `local.properties` (not in code)
- Use HTTPS for all network requests
- Sanitize user input
- Implement certificate pinning for production
- Use Android Keystore for sensitive data

---

## 16. Accessibility

- Add content descriptions to all images
- Ensure minimum touch target size (48dp)
- Support screen readers
- Implement proper focus management
- Test with TalkBack

---

## 17. Documentation

### 17.1 Code Documentation
- KDoc for all public APIs
- Inline comments for complex logic
- README.md with setup instructions

### 17.2 User Documentation
- In-app onboarding
- Help/FAQ section
- Privacy policy

---

## 18. Checklist for Completion

### Core Functionality
- [ ] App launches without crashes
- [ ] Home screen displays trending tracks
- [ ] Search returns results from multiple sources
- [ ] Tracks play with full controls
- [ ] Background playback works
- [ ] Mini player is always visible when playing
- [ ] Full-screen player displays correctly
- [ ] Playlists can be created and managed
- [ ] Favorites system works
- [ ] Downloads work for offline playback

### UI/UX
- [ ] App follows Spotify-like dark theme
- [ ] All screens are responsive
- [ ] Smooth animations and transitions
- [ ] Loading states are shown
- [ ] Error states are handled gracefully
- [ ] Pull-to-refresh works
- [ ] Bottom navigation is functional

### Quality
- [ ] No memory leaks
- [ ] Smooth 60fps scrolling
- [ ] App works offline
- [ ] Unit tests pass
- [ ] No critical lint errors
- [ ] ProGuard/R8 works in release build

### Deployment
- [ ] Debug APK builds successfully
- [ ] Release APK builds successfully
- [ ] CI/CD pipeline passes
- [ ] Code is pushed to GitHub repository

---

## 19. Resources

### API Documentation
- Jamendo: https://developer.jamendo.com/v3.0
- Free Music Archive: https://freemusicarchive.org/api
- MusicBrainz: https://musicbrainz.org/doc/Development/XML_Web_Service/Version_2

### Android Documentation
- Jetpack Compose: https://developer.android.com/jetpack/compose
- ExoPlayer: https://developer.android.com/media/media3/exoplayer
- Room: https://developer.android.com/training/data-storage/room
- Hilt: https://developer.android.com/training/dependency-injection/hilt-android

### Design References
- Material 3: https://m3.material.io/
- Spotify Design: https://spotify.design/

---

**End of Technical Specification**

**Target:** Fully functional SoundFlow Android app built with Kimi K2.5  
**Repository:** https://github.com/AntiGarou/kimi_flow  
**Output:** Working Spotify clone with free music APIs
