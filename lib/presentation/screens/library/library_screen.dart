import 'package:flutter/material.dart';

class LibraryScreen extends StatelessWidget {
  const LibraryScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.library_music, color: Colors.white38, size: 64),
          SizedBox(height: 16),
          Text('Your Library', style: TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold)),
          SizedBox(height: 8),
          Text('Playlists and favorites will appear here',
              style: TextStyle(color: Colors.white54, fontSize: 14)),
        ],
      ),
    );
  }
}
