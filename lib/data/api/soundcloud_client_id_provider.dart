import 'package:dio/dio.dart';
import 'package:html/parser.dart' as html_parser;

class SoundCloudClientIdProvider {
  String? _clientId;
  DateTime? _lastFetch;
  static const _refreshInterval = Duration(hours: 1);

  Future<String?> getClientId() async {
    if (_clientId != null && _lastFetch != null &&
        DateTime.now().difference(_lastFetch!) < _refreshInterval) {
      return _clientId;
    }
    try {
      final dio = Dio();
      final response = await dio.get(
        'https://m.soundcloud.com',
        options: Options(headers: {
          'User-Agent': 'Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 Chrome/120.0.0.0 Mobile Safari/537.36',
          'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
        }),
      );
      final body = response.data.toString();

      var match = RegExp(r'"clientId"\s*:\s*"([^"]+)"').firstMatch(body);
      if (match != null) {
        _clientId = match.group(1);
        _lastFetch = DateTime.now();
        return _clientId;
      }

      match = RegExp(r'client_id=([a-zA-Z0-9]{20,})').firstMatch(body);
      if (match != null) {
        _clientId = match.group(1);
        _lastFetch = DateTime.now();
        return _clientId;
      }

      final document = html_parser.parse(body);
      final scripts = document.querySelectorAll('script[src]');
      for (final script in scripts) {
        final src = script.attributes['src'];
        if (src != null && src.contains('/assets/') && src.endsWith('.js')) {
          try {
            final jsResponse = await dio.get(src);
            final jsMatch = RegExp(r'client_id\s*:\s*"([a-zA-Z0-9]{20,})"').firstMatch(jsResponse.data.toString());
            if (jsMatch != null) {
              _clientId = jsMatch.group(1);
              _lastFetch = DateTime.now();
              return _clientId;
            }
          } catch (_) {}
        }
      }

      return _clientId;
    } catch (_) {
      return _clientId;
    }
  }

  void invalidate() {
    _clientId = null;
    _lastFetch = null;
  }
}
