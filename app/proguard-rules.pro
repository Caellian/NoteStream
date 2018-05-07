# Add project specific ProGuard rules here.
# For more details, see http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment for DEBUGGING
#-dontobfuscate
#-optimizations !desc/simplification/arithmetic,!field/*,!class/merging/*,!desc/allocation/variable
#-keepattributes SourceFile, LineNumberTable

-keep class com.akexorcist.roundcornerprogressbar.** { *; }
-keep interface com.akexorcist.roundcornerprogressbar.** { *; }

-keepclassmembers class com.akexorcist.roundcornerprogressbar.** { *; }
-keepclassmembers class com.akexorcist.roundcornerprogressbar.** { *; }

-keepclasseswithmembers class * {
    @com.akexorcist.roundcornerprogressbar.* <methods>;
}
