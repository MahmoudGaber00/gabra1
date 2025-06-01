-keep class * extends android.app.Activity
-keep class * extends android.app.Application
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider

-keep class com.gabra.deliverybot.** { *; }
-keep class com.gabra.deliverybot.viewmodel.** { *; }

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

-keepclasseswithmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepclassmembers class * extends android.view.View {
    void set*(***);
    *** get*();
}

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

-dontwarn android.webkit.**
-dontwarn androidx.**
