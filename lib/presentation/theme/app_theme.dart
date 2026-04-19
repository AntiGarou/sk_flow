import 'package:flutter/material.dart';
import '../../core/constants.dart';

class AppTheme {
  static ThemeData get darkTheme => ThemeData(
    brightness: Brightness.dark,
    scaffoldBackgroundColor: const Color(AppColors.background),
    colorScheme: const ColorScheme.dark(
      primary: Color(AppColors.primary),
      surface: Color(AppColors.surface),
      onSurface: Color(AppColors.onSurface),
    ),
    cardColor: const Color(AppColors.surfaceVariant),
    bottomNavigationBarTheme: const BottomNavigationBarThemeData(
      backgroundColor: Color(AppColors.surface),
      selectedItemColor: Color(AppColors.primary),
      unselectedItemColor: Color(AppColors.onSurfaceVariant),
      type: BottomNavigationBarType.fixed,
    ),
    appBarTheme: const AppBarTheme(
      backgroundColor: Color(AppColors.background),
      elevation: 0,
    ),
    sliderTheme: SliderThemeData(
      activeTrackColor: const Color(AppColors.primary),
      inactiveTrackColor: const Color(AppColors.onSurfaceVariant),
      thumbColor: const Color(AppColors.primary),
    ),
  );
}
