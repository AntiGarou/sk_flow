import '../../domain/model/track.dart';
import '../../domain/model/artist.dart';
import '../../domain/model/track_source.dart';

class SoundCloudMapper {
  static Track toDomain(Map<String, dynamic> json) {
    final user = json['user'] as Map<String, dynamic>? ?? {};
    final media = json['media'] as Map<String, dynamic>?;
    final transcodings = media?['transcodings'] as List<dynamic>?;

    String? streamUrl;
    if (transcodings != null && transcodings.isNotEmpty) {
      Map<String, dynamic>? progressive;
      for (final t in transcodings) {
        final format = t['format'] as Map<String, dynamic>? ?? {};
        if (format['protocol'] == 'progressive' &&
            (format['mime_type'] as String?)?.startsWith('audio/mpeg') == true) {
          progressive = t as Map<String, dynamic>;
          break;
        }
      }
      progressive ??= transcodings
          .map((t) => t as Map<String, dynamic>)
          .firstWhereOrNull((t) => (t['format'] as Map<String, dynamic>?)?['protocol'] == 'progressive');

      if (progressive != null) {
        streamUrl = progressive['url'] as String? ?? json['stream_url'] as String?;
      }
    }
    streamUrl ??= json['stream_url'] as String?;

    final artwork = json['artwork_url'] as String?;
    final artworkResized = artwork?.replaceFirst('-large.', '-t500x500.');

    return Track(
      id: 'sc_${json['id'] ?? 0}',
      title: json['title'] as String? ?? '',
      artist: Artist(
        id: 'sc_${user['id'] ?? 0}',
        name: user['username'] as String? ?? 'Unknown',
        avatarUrl: user['avatar_url'] as String?,
      ),
      album: null,
      duration: Duration(milliseconds: (json['duration'] as int?) ?? 0),
      streamUrl: streamUrl,
      artworkUrl: artworkResized,
      genre: json['genre'] as String?,
      source: TrackSource.soundcloud,
      isDownloadable: (json['downloadable'] as bool?) == true &&
          (json['has_downloads_left'] as bool?) == true,
    );
  }

  static List<Track> toDomainList(List<dynamic> items) {
    return items.map((e) => toDomain(e as Map<String, dynamic>)).toList();
  }
}

extension _FirstWhereOrNullExtension<T> on Iterable<T> {
  T? firstWhereOrNull(bool Function(T) test) {
    for (final element in this) {
      if (test(element)) return element;
    }
    return null;
  }
}
