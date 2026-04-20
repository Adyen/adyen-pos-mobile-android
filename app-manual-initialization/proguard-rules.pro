# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean

# Fix for kotlin 2.30.10 / AGP 9.1.0 incompatibility
# Workaround for R8 IllegalAccessError on kotlin.collections.CollectionsKt__IterablesKt
# R8 synthesizes a subclass that can't access this package-private stdlib class.
-keep,allowobfuscation,allowshrinking class kotlin.collections.CollectionsKt__IterablesKt
-keep,allowobfuscation,allowshrinking class kotlin.collections.CollectionsKt
-keep class kotlin.collections.** { *; }