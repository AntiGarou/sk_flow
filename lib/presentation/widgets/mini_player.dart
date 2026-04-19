import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../domain/model/playback_state.dart';

class MiniPlayer extends StatelessWidget {
  final PlaybackState state;
  final VoidCallback onTap;
  final VoidCallback onPlayPause;

  const MiniPlayer({super.key, required this.state, required this.onTap, required this.onPlayPause});

  @override
  Widget build(BuildContext context) {
    final track = state.currentTrack;
    if (track == null) return const SizedBox.shrink();

    return GestureDetector(
      onTap: onTap,
      child: Container(
        color: const Color(0xFF181818),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            LinearProgressIndicator(
              value: state.duration.inMilliseconds > 0
                  ? state.position.inMilliseconds / state.duration.inMilliseconds
                  : 0,
              backgroundColor: Colors.white12,
              valueColor: const AlwaysStoppedAnimation<Color>(Color(0xFF1DB954)),
              minHeight: 2,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              child: Row(
                children: [
                  ClipRRect(
                    borderRadius: BorderRadius.circular(4),
                    child: track.artworkUrl != null
                        ? CachedNetworkImage(imageUrl: track.artworkUrl!, width: 40, height: 40, fit: BoxFit.cover)
                        : Container(
                            width: 40,
                            height: 40,
                            color: Colors.grey[800],
                            child: const Icon(Icons.music_note, color: Colors.white38),
                          ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(track.title, maxLines: 1, overflow: TextOverflow.ellipsis,
                            style: const TextStyle(color: Colors.white, fontSize: 14)),
                        Text(track.artist.name, maxLines: 1, overflow: TextOverflow.ellipsis,
                            style: const TextStyle(color: Colors.white54, fontSize: 12)),
                      ],
                    ),
                  ),
                  IconButton(
                    icon: Icon(state.isPlaying ? Icons.pause : Icons.play_arrow),
                    color: Colors.white,
                    onPressed: onPlayPause,
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
