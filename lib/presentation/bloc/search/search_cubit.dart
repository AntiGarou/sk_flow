import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import '../../../domain/model/track.dart';
import '../../../domain/repository/track_repository.dart';

class SearchCubit extends Cubit<SearchState> {
  final TrackRepository _trackRepository;

  SearchCubit(this._trackRepository) : super(SearchInitial());

  Future<void> search(String query) async {
    if (query.isEmpty) {
      emit(SearchInitial());
      return;
    }
    emit(SearchLoading());
    try {
      final tracks = await _trackRepository.searchTracks(query);
      emit(SearchLoaded(tracks));
    } catch (e) {
      emit(SearchError(e.toString()));
    }
  }
}

abstract class SearchState extends Equatable {
  const SearchState();
  @override
  List<Object?> get props => [];
}

class SearchInitial extends SearchState {}

class SearchLoading extends SearchState {}

class SearchLoaded extends SearchState {
  final List<Track> tracks;
  const SearchLoaded(this.tracks);
  @override
  List<Object?> get props => [tracks];
}

class SearchError extends SearchState {
  final String message;
  const SearchError(this.message);
  @override
  List<Object?> get props => [message];
}
