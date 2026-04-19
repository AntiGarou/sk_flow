import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

class LibraryCubit extends Cubit<LibraryState> {
  LibraryCubit() : super(const LibraryState());

  void setTab(int index) => emit(state.copyWith(selectedTab: index));
}

class LibraryState extends Equatable {
  final int selectedTab;
  const LibraryState({this.selectedTab = 0});

  LibraryState copyWith({int? selectedTab}) =>
      LibraryState(selectedTab: selectedTab ?? this.selectedTab);

  @override
  List<Object?> get props => [selectedTab];
}
