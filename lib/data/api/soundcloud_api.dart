import 'package:dio/dio.dart';
import 'soundcloud_client_id_provider.dart';

class SoundCloudApi {
  final Dio _dio;
  final SoundCloudClientIdProvider _clientIdProvider;

  SoundCloudApi(this._clientIdProvider) : _dio = Dio(BaseOptions(
    baseUrl: 'https://api-v2.soundcloud.com',
    connectTimeout: const Duration(seconds: 15),
    receiveTimeout: const Duration(seconds: 15),
  ));

  Future<Map<String, dynamic>> searchTracks(String query, {int limit = 20, int offset = 0}) async {
    final clientId = await _clientIdProvider.getClientId();
    final response = await _dio.get('/search/tracks', queryParameters: {
      'q': query,
      'limit': limit,
      'offset': offset,
      'client_id': clientId,
    });
    return response.data as Map<String, dynamic>;
  }

  Future<List<dynamic>> getTrendingTracks({int limit = 20, int offset = 0}) async {
    final clientId = await _clientIdProvider.getClientId();
    final response = await _dio.get('/tracks', queryParameters: {
      'limit': limit,
      'offset': offset,
      'client_id': clientId,
    });
    return response.data as List<dynamic>;
  }

  Future<Map<String, dynamic>> getTrack(int id) async {
    final clientId = await _clientIdProvider.getClientId();
    final response = await _dio.get('/tracks/$id', queryParameters: {
      'client_id': clientId,
    });
    return response.data as Map<String, dynamic>;
  }

  Future<String?> resolveStreamUrl(String transcodingUrl) async {
    try {
      final clientId = await _clientIdProvider.getClientId();
      if (clientId == null) return null;
      final separator = transcodingUrl.contains('?') ? '&' : '?';
      final url = '$transcodingUrl${separator}client_id=$clientId';
      final response = await _dio.get(url);
      final data = response.data;
      if (data is Map<String, dynamic> && data['url'] != null) {
        return data['url'] as String;
      }
      return null;
    } catch (_) {
      return null;
    }
  }
}
