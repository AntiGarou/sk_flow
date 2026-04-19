import '../model/track.dart';

abstract class TrackRepository {
  Future<List<Track>> searchTracks(String query);
  Future<List<Track>> getTrendingTracks();
  Future<Track?> getTrack(String id);
  Future<List<Track>> getTracksByGenre(String genre);
}
