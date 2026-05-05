# Proguard rules - kept minimal since release isn't minified yet.
# When enabling minify, add rules for kotlinx.serialization, OkHttp, Room.

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# kotlinx.serialization
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep model classes used in serialization
-keep,includedescriptorclasses class br.com.vibetube.app.**$$serializer { *; }
-keepclassmembers class br.com.vibetube.app.** {
    *** Companion;
}
-keepclasseswithmembers class br.com.vibetube.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
