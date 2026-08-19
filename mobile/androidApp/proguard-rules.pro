# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class com.example.ultra.checkout.presentation.screen.PaystackWebView_androidKt {
   public *;
}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Kotlinx Serialization metadata and serializers
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep the generated serializer fields and methods
-keepclassmembers class * {
    *** Companion;
    *** \$serializer;
}

# Prevent R8 from obfuscating synthetic lambda methods used by Kotlinx Serialization
-keepclassmembers class * {
    *** *lambda*(...);
}
