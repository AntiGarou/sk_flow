import 'package:flutter/material.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.all(16),
      children: const [
        Text('Settings', style: TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold)),
        SizedBox(height: 24),
        ListTile(
          leading: Icon(Icons.music_note, color: Color(0xFF1DB954)),
          title: Text('Audio Quality', style: TextStyle(color: Colors.white)),
          subtitle: Text('Normal (128 kbps)', style: TextStyle(color: Colors.white54)),
        ),
        ListTile(
          leading: Icon(Icons.download, color: Color(0xFF1DB954)),
          title: Text('Download Quality', style: TextStyle(color: Colors.white)),
          subtitle: Text('High (320 kbps)', style: TextStyle(color: Colors.white54)),
        ),
        ListTile(
          leading: Icon(Icons.dark_mode, color: Color(0xFF1DB954)),
          title: Text('Dark Mode', style: TextStyle(color: Colors.white)),
          subtitle: Text('Always on', style: TextStyle(color: Colors.white54)),
        ),
        ListTile(
          leading: Icon(Icons.info, color: Color(0xFF1DB954)),
          title: Text('About', style: TextStyle(color: Colors.white)),
          subtitle: Text('SoundFlow v1.0.0', style: TextStyle(color: Colors.white54)),
        ),
      ],
    );
  }
}
