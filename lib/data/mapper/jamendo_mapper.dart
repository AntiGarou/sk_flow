import '../../domain/model/track.dart';
import '../../domain/model/artist.dart';
import '../../domain/model/album.dart';
import '../../domain/model/track_source.dart';

class JamendoMapper {
  static Track toDomain(Map<String, dynamic> json) {
    return Track(
      id: 'jm_${json['id'] ?? ''}',
      title: json['name'] as String? ?? '',
      artist: Artist(
        id: 'jm_${json['artist_id'] ?? ''}',
        name: json['artist_name'] as String? ?? 'Unknown',
        avatarUrl: json['artist_image'] as String?,
      ),
      album: json['album_id'] != null
          ? Album(
              id: 'jm_${json['album_id']}',
              title: json['album_name'] as String? ?? '',
              artworkUrl: json['album_image'] as String?,
            )
          : null,
      duration: Duration(seconds: (json['duration'] as int?) ?? 0),
      streamUrl: json['audio'] as String?,
      artworkUrl: json['image'] as String?,
      genre: json['musicinfo']?['tags']?.isNotEmpty == true
          ? json['musicinfo']['tags'][0] as String?
          : null,
      source: TrackSource.jamendo,
    );
  }

  static List<Track> toDomainList(List<dynamic> items) {
    return items.map((e) => toDomain(e as Map<String, dynamic>)).toList();
  }
}
