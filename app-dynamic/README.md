# Example with Dynamic Module

This app is an example on how you could setup the Adyen POS Mobile SDK being loaded from a [Dynamic Feature Delivery](https://developer.android.com/guide/playcore/feature-delivery).

This example consists of the main app module `app-dynamic` and the dynamic module which includes the SDK, `dynamic_sdk`.
The naming of the SDK module uses underscore `_` instead of dash `-` as a [limitation](https://issuetracker.google.com/issues/109923677?pli=1) on the character allowed for a dynamic module name.

The main points to this implementation are:
- Create a dynamic feature module with dependency on the SDK.
- Declare manual SDK initialization on the manifest. 
- Implement your payment logic on the dynamic module.
- Install the feature module dynamically on our main App.
- Start your payment logic on the dynamic module.
- Initialize the SDK and start the payment.