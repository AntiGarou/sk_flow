import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../bloc/home/home_cubit.dart';
import '../../widgets/track_card.dart';
import '../../widgets/track_list_item.dart';
import '../../../domain/model/track.dart';
import '../../bloc/player/player_cubit.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<HomeCubit, HomeState>(
      builder: (context, state) {
        if (state is HomeLoading) {
          return const Center(child: CircularProgressIndicator(color: Color(0xFF1DB954)));
        }
        if (state is HomeError) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(Icons.error_outline, color: Colors.white38, size: 48),
                const SizedBox(height: 16),
                Text(state.message, style: const TextStyle(color: Colors.white54)),
                const SizedBox(height: 16),
                ElevatedButton(
                  onPressed: () => context.read<HomeCubit>().loadTrending(),
                  style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF1DB954)),
                  child: const Text('Retry'),
                ),
              ],
            ),
          );
        }
        if (state is HomeLoaded) {
          final tracks = state.tracks;
          return RefreshIndicator(
            color: const Color(0xFF1DB954),
            backgroundColor: const Color(0xFF282828),
            onRefresh: () => context.read<HomeCubit>().loadTrending(),
            child: CustomScrollView(
              slivers: [
                SliverToBoxAdapter(
                  child: Padding(
                    padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
                    child: Text(
                      _greeting(),
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ),
                SliverToBoxAdapter(
                  child: Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text('Trending Now',
                            style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
                        TextButton(
                          onPressed: () {},
                          child: const Text('See all', style: TextStyle(color: Colors.white54)),
                        ),
                      ],
                    ),
                  ),
                ),
                SliverToBoxAdapter(
                  child: SizedBox(
                    height: 200,
                    child: ListView.separated(
                      scrollDirection: Axis.horizontal,
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      itemCount: tracks.length > 10 ? 10 : tracks.length,
                      separatorBuilder: (_, __) => const SizedBox(width: 12),                      itemBuilder: (context, index) {
                        final track = tracks[index];
                        return TrackCard(
                          track: track,
                          onTap: () => _playTrack(context, tracks, index),
                        );
                      },
                    ),
                  ),
                ),
                const SliverToBoxAdapter(
                  child: Padding(
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    child: Text('All Tracks',
                        style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
                  ),
                ),
                SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) {
                      final track = tracks[index];
                      final playerState = context.watch<PlayerCubit>().state;
                      final isCurrent = playerState.currentTrack?.id == track.id;
                      return TrackListItem(
                        track: track,
                        isPlaying: isCurrent && playerState.isPlaying,
                        onTap: () => _playTrack(context, tracks, index),
                      );
                    },
                    childCount: tracks.length,
                  ),
                ),
              ],
            ),
          );
        }
        return const SizedBox.shrink();
      },
    );
  }

  void _playTrack(BuildContext context, List<Track> tracks, int index) {
    context.read<PlayerCubit>().playTracks(tracks, index);
  }

  String _greeting() {
    final hour = DateTime.now().hour;
    if (hour < 12) return 'Good Morning';
    if (hour < 18) return 'Good Afternoon';
    return 'Good Evening';
  }
}
