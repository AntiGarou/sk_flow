import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../bloc/search/search_cubit.dart';
import '../../widgets/track_list_item.dart';
import '../../bloc/player/player_cubit.dart';

class SearchScreen extends StatefulWidget {
  const SearchScreen({super.key});

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  final _controller = TextEditingController();

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(16),
          child: TextField(
            controller: _controller,
            style: const TextStyle(color: Colors.white),
            decoration: InputDecoration(
              hintText: 'Search tracks, artists...',
              hintStyle: const TextStyle(color: Colors.white38),
              prefixIcon: const Icon(Icons.search, color: Colors.white54),
              filled: true,
              fillColor: const Color(0xFF282828),
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(24),
                borderSide: BorderSide.none,
              ),
            ),
            textInputAction: TextInputAction.search,
            onSubmitted: (query) => context.read<SearchCubit>().search(query),
          ),
        ),
        Expanded(
          child: BlocBuilder<SearchCubit, SearchState>(
            builder: (context, state) {
              if (state is SearchInitial) {
                return _buildGenreGrid();
              }
              if (state is SearchLoading) {
                return const Center(child: CircularProgressIndicator(color: Color(0xFF1DB954)));
              }
              if (state is SearchError) {
                return Center(
                  child: Text(state.message, style: const TextStyle(color: Colors.white54)),
                );
              }
              if (state is SearchLoaded) {
                if (state.tracks.isEmpty) {
                  return const Center(
                    child: Text('No results found', style: TextStyle(color: Colors.white54, fontSize: 16)),
                  );
                }
                return ListView.builder(
                  itemCount: state.tracks.length,
                  itemBuilder: (context, index) {
                    final track = state.tracks[index];
                    final playerState = context.watch<PlayerCubit>().state;
                    final isCurrent = playerState.currentTrack?.id == track.id;
                    return TrackListItem(
                      track: track,
                      isPlaying: isCurrent && playerState.isPlaying,
                      onTap: () {
                        context.read<PlayerCubit>().playTracks(state.tracks, index);
                      },
                    );
                  },
                );
              }
              return const SizedBox.shrink();
            },
          ),
        ),
      ],
    );
  }

  Widget _buildGenreGrid() {
    final genres = [
      ('Electronic', Colors.purple),
      ('Rock', Colors.red),
      ('Pop', Colors.pink),
      ('Hip-Hop', Colors.orange),
      ('Jazz', Colors.blue),
      ('Classical', Colors.teal),
      ('Ambient', Colors.indigo),
      ('Folk', Colors.brown),
    ];
    return GridView.builder(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 2,
        childAspectRatio: 2.5,
        crossAxisSpacing: 12,
        mainAxisSpacing: 12,
      ),
      itemCount: genres.length,
      itemBuilder: (context, index) {
        final (genre, color) = genres[index];
        return GestureDetector(
          onTap: () {
            _controller.text = genre;
            context.read<SearchCubit>().search(genre);
          },
          child: Container(
            decoration: BoxDecoration(
              color: color.withValues(alpha: 0.7),
              borderRadius: BorderRadius.circular(8),
            ),
            alignment: Alignment.centerLeft,
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Text(
              genre,
              style: const TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold),
            ),
          ),
        );
      },
    );
  }
}
