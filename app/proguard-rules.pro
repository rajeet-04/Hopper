# ==============================================================
# HOPPER - Maximum Compression ProGuard/R8 Rules
# ==============================================================

# -----------------------------------------------------------------
# 0. R8 FULL MODE (most aggressive optimization)
# -----------------------------------------------------------------
# R8 full mode is enabled by default in AGP 8+. These rules ensure
# maximum shrinking while keeping the app functional.

# -----------------------------------------------------------------
# 1. GENERAL OPTIMIZATIONS
# -----------------------------------------------------------------

# Remove all logging in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}

# Remove Kotlin intrinsics checks (saves ~2-5% code size)
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkNotNull(...);
    public static void checkNotNullParameter(...);
    public static void checkParameterIsNotNull(...);
    public static void checkNotNullExpressionValue(...);
    public static void checkExpressionValueIsNotNull(...);
    public static void checkReturnedValueIsNotNull(...);
    public static void throwUninitializedPropertyAccessException(...);
}

# Obfuscation settings
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Repackage all obfuscated classes into a single package
-repackageclasses ''
-allowaccessmodification
-overloadaggressively

# -----------------------------------------------------------------
# 2. KEEP RULES - App Entry Points
# -----------------------------------------------------------------

# Hilt - Keep generated components
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.EarlyEntryPoint class *
-keep,allowobfuscation,allowshrinking @dagger.hilt.InstallIn class *

# Hilt Workers
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class * extends androidx.hilt.work.HiltWorkerFactory { *; }

# -----------------------------------------------------------------
# 3. ROOM DATABASE
# -----------------------------------------------------------------

# Room entities and DAOs
-keep class com.example.hopper.data.local.db.entity.** { *; }
-keep class com.example.hopper.data.local.db.dao.** { *; }
-keep class com.example.hopper.data.local.db.HopperDatabase { *; }
-keep class com.example.hopper.data.local.db.Converters { *; }

# -----------------------------------------------------------------
# 4. KOTLINX SERIALIZATION
# -----------------------------------------------------------------

# Keep serializable classes (DTOs)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.example.hopper.data.remote.api.dto.**$$serializer { *; }
-keepclassmembers class com.example.hopper.data.remote.api.dto.** {
    *** Companion;
}

# -----------------------------------------------------------------
# 5. RETROFIT / OKHTTP
# -----------------------------------------------------------------

# Retrofit interface methods
-keepattributes Signature
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-keep class com.example.hopper.data.remote.api.HopperApiService { *; }

# OkHttp
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# -----------------------------------------------------------------
# 6. MAPLIBRE GL NATIVE
# -----------------------------------------------------------------

# Keep MapLibre native interface
-keep class org.maplibre.android.** { *; }
-keep class com.mapbox.** { *; }
-dontwarn com.mapbox.**

# -----------------------------------------------------------------
# 7. GOOGLE PLAY SERVICES
# -----------------------------------------------------------------

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# -----------------------------------------------------------------
# 8. COMPOSE
# -----------------------------------------------------------------

# Compose compiler generates code that R8 handles well.
# Only keep @Composable functions referenced via reflection (none in this app).
# Compose navigation arguments
-keep class * implements android.os.Parcelable { *; }

# -----------------------------------------------------------------
# 9. COROUTINES
# -----------------------------------------------------------------

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# -----------------------------------------------------------------
# 10. DATASTORE
# -----------------------------------------------------------------

-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# -----------------------------------------------------------------
# 11. AGGRESSIVE REMOVAL
# -----------------------------------------------------------------

# Remove Kotlin metadata (saves ~100KB)
-dontwarn kotlin.reflect.jvm.internal.**

# Remove unused Apache/javax classes often pulled transitively
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# Remove Kotlin reflect if not used (saves ~2MB if pulled in)
-dontwarn kotlin.reflect.**
-assumenosideeffects class kotlin.jvm.internal.Reflection {
    public static <methods>;
}
