import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../domain/model/playback_state.dart';
import '../../../domain/model/track.dart';
import '../../../services/audio_player_service.dart';

class PlayerCubit extends Cubit<PlaybackState> {
  final AudioPlayerService _audioService;

  PlayerCubit(this._audioService) : super(const PlaybackState()) {
    _audioService.stateStream.listen((s) => emit(s));
  }

  PlaybackState get currentState => _audioService.state;

  void play(Track track) => _audioService.play(track);
  void playTracks(List<Track> tracks, int startIndex) => _audioService.playTracks(tracks, startIndex);
  void pause() => _audioService.pause();
  void resume() => _audioService.resume();
  void next() => _audioService.next();
  void previous() => _audioService.previous();
  void seekTo(Duration position) => _audioService.seekTo(position);
  void setRepeatMode(PlaybackRepeatMode mode) => _audioService.setRepeatMode(mode);
  void setShuffle(bool enabled) => _audioService.setShuffle(enabled);
}
