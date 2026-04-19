import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import '../../../domain/model/track.dart';
import '../../../domain/repository/track_repository.dart';

class HomeCubit extends Cubit<HomeState> {
  final TrackRepository _trackRepository;

  HomeCubit(this._trackRepository) : super(HomeInitial()) {
    loadTrending();
  }

  Future<void> loadTrending() async {
    emit(HomeLoading());
    try {
      final tracks = await _trackRepository.getTrendingTracks();
      emit(HomeLoaded(tracks));
    } catch (e) {
      emit(HomeError(e.toString()));
    }
  }
}

abstract class HomeState extends Equatable {
  const HomeState();
  @override
  List<Object?> get props => [];
}

class HomeInitial extends HomeState {}

class HomeLoading extends HomeState {}

class HomeLoaded extends HomeState {
  final List<Track> tracks;
  const HomeLoaded(this.tracks);
  @override
  List<Object?> get props => [tracks];
}

class HomeError extends HomeState {
  final String message;
  const HomeError(this.message);
  @override
  List<Object?> get props => [message];
}
