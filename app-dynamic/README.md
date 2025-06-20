# Dynamic Module

This app demonstrates the implementation of the Adyen POS Mobile SDK using an Android Dynamic Feature Module. For background information on this Android feature, please see the official documentation: [Dynamic Feature Delivery](https://developer.android.com/guide/playcore/feature-delivery).

## Modules

*   **app-dynamic**: The main application module responsible for requesting and launching the dynamic feature module.
*   **dynamic_sdk** [1](#1): The dynamic feature module that encapsulates the Adyen POS Mobile SDK and the logic to initiate a payment.

## Implementation

This sample builds upon the concepts shown in **app-manual-initialization** (refer to that sample for basic SDK setup) by showcasing how to lazy-load the SDK via a dynamic feature.

The dynamic feature module is configured in the **app-dynamic** module's [AndroidManifest.xml](src/main/AndroidManifest.xml). The Adyen SDK is initialized and a payment process is started within the [PaymentActivity.kt](../dynamic_sdk/src/main/java/com/adyen/sampleapp/dynamic/PaymentActivity.kt) located inside the `dynamic_sdk` module.

When launching an Activity from a dynamic feature module, it's crucial to include the following override in that Activity:
```kotlin
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.install(this)
    }
```
This is required as per the [on-demand feature delivery documentation](https://developer.android.com/guide/playcore/feature-delivery/on-demand) to ensure that XML resources associated with the SDK are correctly linked and accessible.

## Building & Testing

You can build and install the app in two ways:

1.  **Standard Installation (Fat APK):**
    *   The "Run" button in Android Studio for the `app-dynamic` configuration.
        This method installs a single APK where modules are *not* dynamically loaded on demand but are included upfront.

2.  **Testing Dynamic Feature Delivery:**
    To simulate and test the on-demand loading of the `dynamic_sdk` module:
    *   Run the Gradle task: `./gradlew :app-dynamic:installDynamicDebugApp`

    **Note:** The `installDynamicDebugApp` task is provided for convenience but may not be actively maintained. For the most up-to-date methods on testing dynamic delivery locally, **it is highly recommended to refer to the official Android documentation** on [testing dynamic delivery](https://developer.android.com/guide/playcore/feature-delivery/on-demand#local-testing) and the official [Google Play App Bundle samples](https://github.com/android/app-bundle-samples/tree/main/DynamicFeatures#testing-dynamic-delivery).

When the app runs and the dynamic module is loaded, `PaymentActivity` will be launched, demonstrating the dynamically loaded SDK functionality. 
Toast messages will provide feedback to the user of this process. 

---

#### 1
The naming of the SDK module `dynamic_sdk` uses an underscore (`_`) instead of a dash (`-`) due to a known [limitation](https://issuetracker.google.com/issues/109923677?pli=1) on characters allowed for dynamic feature module names.