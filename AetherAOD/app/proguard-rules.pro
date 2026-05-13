# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepclassmembers class * {
    @dagger.* <methods>;
}
-dontwarn dagger.internal.**
-dontwarn javax.inject.**
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
-keep class * extends androidx.compose.runtime.Composable
