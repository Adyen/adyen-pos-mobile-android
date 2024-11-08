# Manual Initialization

Automatic initialization of the SDK is simple and covers most of the cases but 
if initialization must be postponed and to achieve fine control over it, 
there is a manual way of initializing of the SDK.

## Disable Auto Initialization

A specific `provider` block is added at the end of the application definition in [AndroidManifest.xml](src/main/AndroidManifest.xml):
```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
        android:name="com.adyen.ipp.InPersonPaymentsInitializer"
        tools:node="remove" />
</provider>
```
which disables the auto initialization for the application.

## Check Initialization Status

Current initialization state can be observed via `InPersonPayments.initialised` state flow. 

For example in [PaymentSampleAppFragment.kt](src/main/java/com/adyen/sampleapp/PaymentSampleAppFragment.kt) line 112
this flow is used to disable buttons until the SDK is ready.

## Manual SDK Initialization

To manually initialize the SDK a simple code must be executed:
```kotlin
AppInitializer.getInstance(requireContext())
    .initializeComponent(InPersonPaymentsInitializer::class.java)
```

see [PaymentSampleAppFragment.kt](src/main/java/com/adyen/sampleapp/PaymentSampleAppFragment.kt) line 124.

# Authentication

See [MyAuthenticationService.kt](src/main/java/com/adyen/sampleapp/MyAuthenticationService.kt) 
for example of how the authentication code can be implemented.