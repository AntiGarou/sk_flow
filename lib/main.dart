import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'data/api/soundcloud_client_id_provider.dart';
import 'data/api/soundcloud_api.dart';
import 'data/api/jamendo_api.dart';
import 'data/repository/track_repository_impl.dart';
import 'domain/repository/track_repository.dart';
import 'services/audio_player_service.dart';
import 'presentation/bloc/player/player_cubit.dart';
import 'presentation/bloc/home/home_cubit.dart';
import 'presentation/bloc/search/search_cubit.dart';
import 'presentation/bloc/library/library_cubit.dart';
import 'presentation/theme/app_theme.dart';
import 'presentation/screens/home/home_screen.dart';
import 'presentation/screens/search/search_screen.dart';
import 'presentation/screens/library/library_screen.dart';
import 'presentation/screens/settings/settings_screen.dart';
import 'presentation/screens/player/full_screen_player.dart';
import 'presentation/widgets/mini_player.dart';
import 'domain/model/playback_state.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const SoundFlowApp());
}

class SoundFlowApp extends StatelessWidget {
  const SoundFlowApp({super.key});

  @override
  Widget build(BuildContext context) {
    final clientIdProvider = SoundCloudClientIdProvider();
    final soundCloudApi = SoundCloudApi(clientIdProvider);
    final jamendoApi = JamendoApi('');
    final trackRepository = TrackRepositoryImpl(soundCloudApi, jamendoApi);
    final audioService = AudioPlayerService(soundCloudApi);

    return MultiRepositoryProvider(
      providers: [
        RepositoryProvider<TrackRepository>.value(value: trackRepository),
      ],
      child: MultiBlocProvider(
        providers: [
          BlocProvider(create: (_) => PlayerCubit(audioService)),
          BlocProvider(create: (_) => HomeCubit(trackRepository)),
          BlocProvider(create: (_) => SearchCubit(trackRepository)),
          BlocProvider(create: (_) => LibraryCubit()),
        ],
        child: MaterialApp(
          title: 'SoundFlow',
          debugShowCheckedModeBanner: false,
          theme: AppTheme.darkTheme,
          home: const MainNavigation(),
        ),
      ),
    );
  }
}

class MainNavigation extends StatefulWidget {
  const MainNavigation({super.key});

  @override
  State<MainNavigation> createState() => _MainNavigationState();
}

class _MainNavigationState extends State<MainNavigation> {
  int _currentIndex = 0;
  bool _showFullPlayer = false;

  final _screens = const [
    HomeScreen(),
    SearchScreen(),
    LibraryScreen(),
    SettingsScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          IndexedStack(
            index: _currentIndex,
            children: _screens,
          ),
          Positioned(
            left: 0,
            right: 0,
            bottom: 56,
            child: BlocBuilder<PlayerCubit, PlaybackState>(
              builder: (context, state) {
                if (state.currentTrack == null) return const SizedBox.shrink();
                return MiniPlayer(
                  state: state,
                  onTap: () => setState(() => _showFullPlayer = true),
                  onPlayPause: () {
                    if (state.isPlaying) {
                      context.read<PlayerCubit>().pause();
                    } else {
                      context.read<PlayerCubit>().resume();
                    }
                  },
                );
              },
            ),
          ),
          if (_showFullPlayer)
            Positioned.fill(
              child: FullScreenPlayer(
                onClose: () => setState(() => _showFullPlayer = false),
              ),
            ),
        ],
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: (index) => setState(() => _currentIndex = index),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Home'),
          BottomNavigationBarItem(icon: Icon(Icons.search), label: 'Search'),
          BottomNavigationBarItem(icon: Icon(Icons.library_music), label: 'Library'),
          BottomNavigationBarItem(icon: Icon(Icons.settings), label: 'Settings'),
        ],
      ),
    );
  }
}
