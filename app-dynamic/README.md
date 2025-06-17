# Dynamic Module

This app is an example of the Adyen POS Mobile SDK being implemented via Dynamic Feature Module.
See Android documentation for background information:[Dynamic Feature Delivery](https://developer.android.com/guide/playcore/feature-delivery).

## Modules
* `app-dynamic` - Main app module used to load the dynamic module
* `dynamic_sdk`[1] - Implements the sdk and is configured to start a payment.

## Implementation
Builds on [app-manual-initialization](../app-manual-initialization) to lazy load the sdk.
This is configured in the [AndroidManifest.xml](src/main/AndroidManifest.xml) and initialized in [PaymentActivity.kt](../dynamic_sdk/src/main/java/com/adyen/sampleapp/dynamic/PaymentActivity.kt).

Because this implementation launches the [PaymentActivity.kt](../dynamic_sdk/src/main/java/com/adyen/sampleapp/dynamic/PaymentActivity.kt).
It's important to have:
```
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.install(this)
    }
```
As per the documentation for [on demand feature delivery](https://developer.android.com/guide/playcore/feature-delivery/on-demand), otherwise xml resources associated with the sdk are not linked.

## Building & Testing 
`./gradlew :app-dynamic:installDebug`: Similar to pressing the "run" button, gradle will install a "fat" apk where modules are _not dynamically loaded_.

To test dynamic feature delivery a convenience gradle task has been added to the project.
`./gradlew :app-dynamic:installDynamicDebugApp`

The `installDynamicDebugApp` task will not be actively maintained, so it is recommended to refer to the [documentation](https://developer.android.com/guide/playcore/feature-delivery/on-demand#local-testing) 
and [sample project](https://github.com/android/app-bundle-samples/tree/main/DynamicFeatures#testing-dynamic-delivery).

[1] The naming of the SDK module uses underscore `_` instead of dash `-` as a [limitation](https://issuetracker.google.com/issues/109923677?pli=1) on the character allowed for a dynamic module name.
