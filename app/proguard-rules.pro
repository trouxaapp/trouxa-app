# Retrofit + Gson rely on reflection over these DTO packages. R8 renaming
# their fields makes Gson return null-filled objects, which would silently
# degrade the release build to empty data. Keep them all.
-keepattributes Signature
-keepattributes *Annotation*

# Camara/BCB API DTOs
-keep class com.trouxaapp.app.data.remote.** { *; }
# Worker-level remote schemas (feed.json)
-keep class com.trouxaapp.app.worker.FeedRemoteData { *; }
-keep class com.trouxaapp.app.worker.FeedRemoteItem { *; }
# Repository-level remote schemas (curadoria.json) and Gson-serialized helpers
-keep class com.trouxaapp.app.data.repository.LeisRepository$CuradoriaRemote { *; }
-keep class com.trouxaapp.app.data.repository.LeisRepository$CuradoriaEntry { *; }
-keep class com.trouxaapp.app.data.repository.LeisRepository$FonteCuradoria { *; }
-keep class com.trouxaapp.app.data.repository.LeisRepository$AutorInfo { *; }
# Room entities serialized to JSON columns
-keep class com.trouxaapp.app.data.local.** { *; }

# Gson internals
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Retrofit interface methods carry generic signatures R8 must not strip
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
