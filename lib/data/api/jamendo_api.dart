import 'package:dio/dio.dart';

class JamendoApi {
  final Dio _dio;
  final String clientId;

  JamendoApi(this.clientId) : _dio = Dio(BaseOptions(
    baseUrl: 'https://api.jamendo.com/v3.0',
    connectTimeout: const Duration(seconds: 15),
    receiveTimeout: const Duration(seconds: 15),
  ));

  Future<Map<String, dynamic>> searchTracks(String query, {int limit = 20}) async {
    final response = await _dio.get('/tracks', queryParameters: {
      'client_id': clientId,
      'format': 'json',
      'limit': limit,
      'search': query,
    });
    return response.data as Map<String, dynamic>;
  }

  Future<Map<String, dynamic>> getTracks({int limit = 20}) async {
    final response = await _dio.get('/tracks', queryParameters: {
      'client_id': clientId,
      'format': 'json',
      'limit': limit,
    });
    return response.data as Map<String, dynamic>;
  }

  Future<Map<String, dynamic>> getTracksByGenre(String genre, {int limit = 20}) async {
    final response = await _dio.get('/tracks', queryParameters: {
      'client_id': clientId,
      'format': 'json',
      'limit': limit,
      'tags': genre,
    });
    return response.data as Map<String, dynamic>;
  }
}
