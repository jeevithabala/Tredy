#This is a configuration file for ProGuard

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose



-optimizationpasses 5
-allowaccessmodification




-keep public class * extends android.app.Activity
-keep public class * extends android.support.v7.app.AppCompatActivity
-keep public class * extends android.app.Application

-keep public class * extends android.supports.v4.app.Fragment

-keepclasseswithmembernames class *{
  native <methods>;
 }


-keep public class * extends android.view.View{

 public <init>(android.content.Context);
 public <init>(android.content.Context, android.util.AttributeSet);
 public <init>(android.content.Context, android.util.AttributeSet, int);
 public void set*(...);
}

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

-keepclassmembers class * extends android.app.Activity {
 public void *(android.view.View);
}

-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}


-keepclassmembers class **.R$* {
 public static <fields>;
}
-dontwarn android.support.v4.**
#-keep class android.os.Handler
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
#-keep class com.mixpanel.android.** { *;}
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

-keep class com.marmeto.user.tredy.category.model.** {*;}







# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn android.support.**
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**


-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }

-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement


-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-dontwarn com.squareup.okhttp.**
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

