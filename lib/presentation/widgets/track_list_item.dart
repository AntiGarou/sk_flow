import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../domain/model/track.dart';

class TrackListItem extends StatelessWidget {
  final Track track;
  final VoidCallback onTap;
  final bool isPlaying;

  const TrackListItem({super.key, required this.track, required this.onTap, this.isPlaying = false});

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: ClipRRect(
        borderRadius: BorderRadius.circular(4),
        child: track.artworkUrl != null
            ? CachedNetworkImage(
                imageUrl: track.artworkUrl!,
                width: 48,
                height: 48,
                fit: BoxFit.cover,
                placeholder: (_, __) => Container(
                  width: 48,
                  height: 48,
                  color: Colors.grey[800],
                  child: const Icon(Icons.music_note, color: Colors.white38),
                ),
                errorWidget: (_, __, ___) => Container(
                  width: 48,
                  height: 48,
                  color: Colors.grey[800],
                  child: const Icon(Icons.music_note, color: Colors.white38),
                ),
              )
            : Container(
                width: 48,
                height: 48,
                color: Colors.grey[800],
                child: const Icon(Icons.music_note, color: Colors.white38),
              ),
      ),
      title: Text(
        track.title,
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
        style: TextStyle(
          color: isPlaying ? const Color(0xFF1DB954) : Colors.white,
          fontWeight: isPlaying ? FontWeight.bold : FontWeight.normal,
        ),
      ),
      subtitle: Text(
        track.artist.name,
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
        style: const TextStyle(color: Colors.white54),
      ),
      trailing: isPlaying
          ? const Icon(Icons.equalizer, color: Color(0xFF1DB954))
          : Text(
              _formatDuration(track.duration),
              style: const TextStyle(color: Colors.white38, fontSize: 12),
            ),
      onTap: onTap,
    );
  }

  String _formatDuration(Duration d) {
    final m = d.inMinutes;
    final s = d.inSeconds.remainder(60);
    return '$m:${s.toString().padLeft(2, '0')}';
  }
}
