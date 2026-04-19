import 'dart:async';
import '../../domain/model/track.dart';
import '../../domain/repository/track_repository.dart';
import '../api/soundcloud_api.dart';
import '../api/jamendo_api.dart';
import '../mapper/soundcloud_mapper.dart';
import '../mapper/jamendo_mapper.dart';

class TrackRepositoryImpl implements TrackRepository {
  final SoundCloudApi _soundCloudApi;
  final JamendoApi? _jamendoApi;

  TrackRepositoryImpl(this._soundCloudApi, this._jamendoApi);

  @override
  Future<List<Track>> searchTracks(String query) async {
    final results = <Track>[];
    await Future.wait([
      _searchSoundCloud(query).then((tracks) => results.addAll(tracks)).catchError((_) => <Track>[]),
      _searchJamendo(query).then((tracks) => results.addAll(tracks)).catchError((_) => <Track>[]),
    ]);
    return results;
  }

  @override
  Future<List<Track>> getTrendingTracks() async {
    final results = <Track>[];
    await Future.wait([
      _trendingSoundCloud().then((tracks) => results.addAll(tracks)).catchError((_) => <Track>[]),
      _trendingJamendo().then((tracks) => results.addAll(tracks)).catchError((_) => <Track>[]),
    ]);
    return results;
  }

  @override
  Future<Track?> getTrack(String id) async {
    if (id.startsWith('sc_')) {
      try {
        final scId = int.tryParse(id.replaceFirst('sc_', ''));
        if (scId == null) return null;
        final json = await _soundCloudApi.getTrack(scId);
        final track = SoundCloudMapper.toDomain(json);
        if (track.streamUrl != null && track.streamUrl!.contains('api-v2.soundcloud.com')) {
          final resolvedUrl = await _soundCloudApi.resolveStreamUrl(track.streamUrl!);
          if (resolvedUrl != null) {
            return track.copyWith(streamUrl: resolvedUrl);
          }
        }
        return track;
      } catch (_) {
        return null;
      }
    }
    return null;
  }

  @override
  Future<List<Track>> getTracksByGenre(String genre) async {
    final results = <Track>[];
    await Future.wait([
      _searchSoundCloud(genre).then((tracks) => results.addAll(tracks)).catchError((_) => <Track>[]),
      _genreJamendo(genre).then((tracks) => results.addAll(tracks)).catchError((_) => <Track>[]),
    ]);
    return results;
  }

  Future<List<Track>> _searchSoundCloud(String query) async {
    final data = await _soundCloudApi.searchTracks(query);
    final collection = data['collection'] as List<dynamic>? ?? [];
    return SoundCloudMapper.toDomainList(collection);
  }

  Future<List<Track>> _searchJamendo(String query) async {
    final jamendo = _jamendoApi;
    if (jamendo == null) return [];
    final data = await jamendo.searchTracks(query);
    final results = data['results'] as List<dynamic>? ?? [];
    return JamendoMapper.toDomainList(results);
  }

  Future<List<Track>> _trendingSoundCloud() async {
    final data = await _soundCloudApi.getTrendingTracks();
    return SoundCloudMapper.toDomainList(data);
  }

  Future<List<Track>> _trendingJamendo() async {
    final jamendo = _jamendoApi;
    if (jamendo == null) return [];
    final data = await jamendo.getTracks();
    final results = data['results'] as List<dynamic>? ?? [];
    return JamendoMapper.toDomainList(results);
  }

  Future<List<Track>> _genreJamendo(String genre) async {
    final jamendo = _jamendoApi;
    if (jamendo == null) return [];
    final data = await jamendo.getTracksByGenre(genre);
    final results = data['results'] as List<dynamic>? ?? [];
    return JamendoMapper.toDomainList(results);
  }
}
