import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../domain/model/track.dart';

class TrackCard extends StatelessWidget {
  final Track track;
  final VoidCallback onTap;

  const TrackCard({super.key, required this.track, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: SizedBox(
        width: 140,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(8),
              child: track.artworkUrl != null
                  ? CachedNetworkImage(
                      imageUrl: track.artworkUrl!,
                      width: 140,
                      height: 140,
                      fit: BoxFit.cover,
                      placeholder: (_, __) => Container(
                        width: 140,
                        height: 140,
                        color: Colors.grey[800],
                        child: const Icon(Icons.music_note, color: Colors.white38, size: 40),
                      ),
                      errorWidget: (_, __, ___) => Container(
                        width: 140,
                        height: 140,
                        color: Colors.grey[800],
                        child: const Icon(Icons.music_note, color: Colors.white38, size: 40),
                      ),
                    )
                  : Container(
                      width: 140,
                      height: 140,
                      decoration: BoxDecoration(
                        color: Colors.grey[800],
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: const Icon(Icons.music_note, color: Colors.white38, size: 40),
                    ),
            ),
            const SizedBox(height: 8),
            Text(
              track.title,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: const TextStyle(color: Colors.white, fontSize: 13, fontWeight: FontWeight.w600),
            ),
            Text(
              track.artist.name,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: const TextStyle(color: Colors.white54, fontSize: 12),
            ),
          ],
        ),
      ),
    );
  }
}
