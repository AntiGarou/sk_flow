import 'artist.dart';
import 'album.dart';
import 'track_source.dart';

class Track {
  final String id;
  final String title;
  final Artist artist;
  final Album? album;
  final Duration duration;
  final String? streamUrl;
  final String? artworkUrl;
  final String? genre;
  final String source;
  final bool isDownloadable;
  final bool isFavorite;

  const Track({
    required this.id,
    required this.title,
    required this.artist,
    this.album,
    this.duration = Duration.zero,
    this.streamUrl,
    this.artworkUrl,
    this.genre,
    this.source = TrackSource.soundcloud,
    this.isDownloadable = false,
    this.isFavorite = false,
  });

  Track copyWith({
    String? streamUrl,
    bool? isFavorite,
  }) {
    return Track(
      id: id,
      title: title,
      artist: artist,
      album: album,
      duration: duration,
      streamUrl: streamUrl ?? this.streamUrl,
      artworkUrl: artworkUrl,
      genre: genre,
      source: source,
      isDownloadable: isDownloadable,
      isFavorite: isFavorite ?? this.isFavorite,
    );
  }
}
