# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK tools proguard configuration.

# Keep Markwon classes
-keep class io.noties.markwon.** { *; }
-keep class org.commonmark.** { *; }

# Keep jlatexmath classes (LaTeX / Math rendering)
-keep class org.scilab.forge.jlatexmath.** { *; }
-keep class ru.noties.jlatexmath.** { *; }
-keepattributes *Annotation*
-keepattributes Signature
