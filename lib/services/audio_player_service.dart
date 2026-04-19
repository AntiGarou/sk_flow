import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:just_audio/just_audio.dart';
import '../domain/model/track.dart';
import '../domain/model/playback_state.dart' as app_state;
import '../domain/model/track_source.dart';
import '../data/api/soundcloud_api.dart';

class AudioPlayerService {
  final AudioPlayer _player = AudioPlayer();
  final SoundCloudApi _soundCloudApi;
  final _stateController = StreamController<app_state.PlaybackState>.broadcast();

  app_state.PlaybackState _state = const app_state.PlaybackState();
  List<Track> _queue = [];
  int _queueIndex = 0;

  AudioPlayerService(this._soundCloudApi) {
    _init();
  }

  Stream<app_state.PlaybackState> get stateStream => _stateController.stream;
  app_state.PlaybackState get state => _state;

  void _init() {
    _player.playbackEventStream.map(_mapEvent).listen((state) {
      _state = state;
      _stateController.add(_state);
    });
  }

  app_state.PlaybackState _mapEvent(PlaybackEvent event) {
    return _state.copyWith(
      isPlaying: _player.playing,
      position: _player.position,
      duration: _player.duration ?? Duration.zero,
    );
  }

  Future<void> play(Track track) async {
    _queue = [track];
    _queueIndex = 0;
    _state = _state.copyWith(currentTrack: track, queue: _queue, queueIndex: 0);
    _stateController.add(_state);
    await _playTrack(track);
  }

  Future<void> playTracks(List<Track> tracks, int startIndex) async {
    _queue = tracks;
    _queueIndex = startIndex;
    final track = tracks[startIndex];
    _state = _state.copyWith(currentTrack: track, queue: _queue, queueIndex: startIndex);
    _stateController.add(_state);
    await _playTrack(track);
  }

  Future<void> _playTrack(Track track) async {
    var streamUrl = track.streamUrl;
    if (streamUrl == null) return;

    if (track.source == TrackSource.soundcloud && streamUrl.contains('api-v2.soundcloud.com')) {
      final resolvedUrl = await _soundCloudApi.resolveStreamUrl(streamUrl);
      if (resolvedUrl != null) {
        streamUrl = resolvedUrl;
      }
    }

    try {
      await _player.setUrl(streamUrl);
      await _player.play();
    } catch (e) {
      debugPrint('AudioPlayerService: playback error: $e');
    }
  }

  Future<void> pause() async => await _player.pause();
  Future<void> resume() async => await _player.play();
  Future<void> stop() async {
    await _player.stop();
    _state = const app_state.PlaybackState();
    _stateController.add(_state);
  }

  Future<void> seekTo(Duration position) async => await _player.seek(position);

  Future<void> next() async {
    if (_queueIndex < _queue.length - 1) {
      _queueIndex++;
      final track = _queue[_queueIndex];
      _state = _state.copyWith(currentTrack: track, queueIndex: _queueIndex);
      _stateController.add(_state);
      await _playTrack(track);
    }
  }

  Future<void> previous() async {
    if (_player.position > const Duration(seconds: 3)) {
      await _player.seek(Duration.zero);
    } else if (_queueIndex > 0) {
      _queueIndex--;
      final track = _queue[_queueIndex];
      _state = _state.copyWith(currentTrack: track, queueIndex: _queueIndex);
      _stateController.add(_state);
      await _playTrack(track);
    }
  }

  void setRepeatMode(app_state.PlaybackRepeatMode mode) {
    switch (mode) {
      case app_state.PlaybackRepeatMode.off:
        _player.setLoopMode(LoopMode.off);
      case app_state.PlaybackRepeatMode.one:
        _player.setLoopMode(LoopMode.one);
      case app_state.PlaybackRepeatMode.all:
        _player.setLoopMode(LoopMode.all);
    }
    _state = _state.copyWith(repeatMode: mode);
    _stateController.add(_state);
  }

  void setShuffle(bool enabled) {
    _player.setShuffleModeEnabled(enabled);
    _state = _state.copyWith(shuffleEnabled: enabled);
    _stateController.add(_state);
  }

  void dispose() {
    _player.dispose();
    _stateController.close();
  }
}
