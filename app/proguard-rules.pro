# Add project specific ProGuard rules here.

# Retrofit
-keepattributes Signature
-keepattributes Exceptions

# Kotlinx Serialization
-keepattributes *Annotation*
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
