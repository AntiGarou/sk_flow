import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../bloc/player/player_cubit.dart';
import '../../../domain/model/playback_state.dart' show PlaybackState, PlaybackRepeatMode;

class FullScreenPlayer extends StatefulWidget {
  final VoidCallback onClose;

  const FullScreenPlayer({super.key, required this.onClose});

  @override
  State<FullScreenPlayer> createState() => _FullScreenPlayerState();
}

class _FullScreenPlayerState extends State<FullScreenPlayer> with SingleTickerProviderStateMixin {
  late AnimationController _animController;
  bool _dragging = false;
  double _dragValue = 0;

  @override
  void initState() {
    super.initState();
    _animController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 300),
    );
    _animController.forward();
  }

  @override
  void dispose() {
    _animController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<PlayerCubit, PlaybackState>(
      builder: (context, state) {
        final track = state.currentTrack;
        if (track == null) return const SizedBox.shrink();

        return SlideTransition(
          position: Tween<Offset>(
            begin: const Offset(0, 1),
            end: Offset.zero,
          ).animate(CurvedAnimation(parent: _animController, curve: Curves.easeOut)),
          child: Container(
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                colors: [Color(0xFF1a1a2e), Color(0xFF121212)],
              ),
            ),
            child: SafeArea(
              child: Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        IconButton(
                          icon: const Icon(Icons.keyboard_arrow_down, color: Colors.white, size: 32),
                          onPressed: widget.onClose,
                        ),
                        Column(
                          children: [
                            const Text('NOW PLAYING',
                                style: TextStyle(color: Colors.white54, fontSize: 10, letterSpacing: 1.5)),
                            Text(track.source,
                                style: const TextStyle(color: Colors.white70, fontSize: 12)),
                          ],
                        ),
                        IconButton(
                          icon: const Icon(Icons.more_horiz, color: Colors.white),
                          onPressed: () {},
                        ),
                      ],
                    ),
                  ),
                  const Spacer(flex: 1),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 32),
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(12),
                      child: AspectRatio(
                        aspectRatio: 1,
                        child: track.artworkUrl != null
                            ? CachedNetworkImage(
                                imageUrl: track.artworkUrl!,
                                fit: BoxFit.cover,
                                placeholder: (_, __) => Container(
                                  color: Colors.grey[800],
                                  child: const Icon(Icons.music_note, color: Colors.white38, size: 64),
                                ),
                                errorWidget: (_, __, ___) => Container(
                                  color: Colors.grey[800],
                                  child: const Icon(Icons.music_note, color: Colors.white38, size: 64),
                                ),
                              )
                            : Container(
                                color: Colors.grey[800],
                                child: const Icon(Icons.music_note, color: Colors.white38, size: 64),
                              ),
                      ),
                    ),
                  ),
                  const Spacer(flex: 1),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 32),
                    child: Column(
                      children: [
                        Row(
                          children: [
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(track.title,
                                      maxLines: 1,
                                      overflow: TextOverflow.ellipsis,
                                      style: const TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.bold)),
                                  const SizedBox(height: 4),
                                  Text(track.artist.name,
                                      maxLines: 1,
                                      overflow: TextOverflow.ellipsis,
                                      style: const TextStyle(color: Colors.white54, fontSize: 16)),
                                ],
                              ),
                            ),
                            IconButton(
                              icon: Icon(
                                track.isFavorite ? Icons.favorite : Icons.favorite_border,
                                color: track.isFavorite ? const Color(0xFF1DB954) : Colors.white54,
                              ),
                              onPressed: () {},
                            ),
                          ],
                        ),
                        const SizedBox(height: 16),
                        _buildSlider(state),
                        const SizedBox(height: 8),
                        _buildControls(state),
                        const SizedBox(height: 8),
                        _buildBottomControls(state),
                      ],
                    ),
                  ),
                  const SizedBox(height: 24),
                ],
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _buildSlider(PlaybackState state) {
    final posMs = state.position.inMilliseconds.toDouble();
    final durMs = state.duration.inMilliseconds.toDouble().clamp(1, double.infinity);
    final value = _dragging ? _dragValue : (posMs / durMs).clamp(0.0, 1.0);

    return Row(
      children: [
        SizedBox(
          width: 40,
          child: Text(_formatDuration(state.position),
              style: const TextStyle(color: Colors.white54, fontSize: 12)),
        ),
        Expanded(
          child: SliderTheme(
            data: SliderThemeData(
              trackHeight: 3,
              thumbShape: const RoundSliderThumbShape(enabledThumbRadius: 6),
              overlayShape: const RoundSliderOverlayShape(overlayRadius: 12),
            ),
            child: Slider(
              value: value,
              onChanged: (v) {
                setState(() {
                  _dragging = true;
                  _dragValue = v;
                });
              },
              onChangeEnd: (v) {
                final position = Duration(milliseconds: (v * state.duration.inMilliseconds).round());
                context.read<PlayerCubit>().seekTo(position);
                setState(() => _dragging = false);
              },
            ),
          ),
        ),
        SizedBox(
          width: 40,
          child: Text(_formatDuration(state.duration),
              style: const TextStyle(color: Colors.white54, fontSize: 12)),
        ),
      ],
    );
  }

  Widget _buildControls(PlaybackState state) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        IconButton(
          icon: Icon(
            state.shuffleEnabled ? Icons.shuffle : Icons.shuffle,
            color: state.shuffleEnabled ? const Color(0xFF1DB954) : Colors.white54,
          ),
          iconSize: 24,
          onPressed: () => context.read<PlayerCubit>().setShuffle(!state.shuffleEnabled),
        ),
        IconButton(
          icon: const Icon(Icons.skip_previous, color: Colors.white),
          iconSize: 36,
          onPressed: () => context.read<PlayerCubit>().previous(),
        ),
        Container(
          decoration: const BoxDecoration(
            color: Color(0xFF1DB954),
            shape: BoxShape.circle,
          ),
          child: IconButton(
            icon: Icon(state.isPlaying ? Icons.pause : Icons.play_arrow, color: Colors.black),
            iconSize: 40,
            onPressed: () {
              if (state.isPlaying) {
                context.read<PlayerCubit>().pause();
              } else {
                context.read<PlayerCubit>().resume();
              }
            },
          ),
        ),
        IconButton(
          icon: const Icon(Icons.skip_next, color: Colors.white),
          iconSize: 36,
          onPressed: () => context.read<PlayerCubit>().next(),
        ),
        IconButton(
          icon: Icon(
            state.repeatMode == PlaybackRepeatMode.one
                ? Icons.repeat_one
                : Icons.repeat,
            color: state.repeatMode != PlaybackRepeatMode.off
                ? const Color(0xFF1DB954)
                : Colors.white54,
          ),
          iconSize: 24,
          onPressed: () {
            final modes = PlaybackRepeatMode.values;
            final current = modes.indexOf(state.repeatMode);
            final next = modes[(current + 1) % modes.length];
            context.read<PlayerCubit>().setRepeatMode(next);
          },
        ),
      ],
    );
  }

  Widget _buildBottomControls(PlaybackState state) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        IconButton(
          icon: const Icon(Icons.playlist_play, color: Colors.white54),
          onPressed: () {},
        ),
        IconButton(
          icon: const Icon(Icons.share, color: Colors.white54),
          onPressed: () {},
        ),
      ],
    );
  }

  String _formatDuration(Duration d) {
    final m = d.inMinutes;
    final s = d.inSeconds.remainder(60);
    return '$m:${s.toString().padLeft(2, '0')}';
  }
}
