# Adyen POS Mobile SDK for Android

## Introduction

This is the home page of the Adyen POS Mobile SDK for Android. 
Our mobile solution lets you accept in-person payments on an Android device, using a card reader that is connected via Bluetooth.

The repository contains the following sample applications: 

1. `app-default` is an example of automatic initialization of the SDK. 
 See [README.md](app-default/README.md).

2. `app-manual-initialization` is a more advanced example of manual initialization of the SDK. 
 See [README.md](app-manual-initialization/README.md)

## Artifactory Setup

To build the project you need to define some variables in your `local.properties` file locally.
Documentation on how to generate the API Key to access the Repository can be found [here](https://docs.adyen.com/point-of-sale/ipp-mobile/tap-to-pay-android/integration-ttp/)
Also, see the `settings.gradle` file on how to switch between the TEST and the LIVE repositories.
```
# URL and credentials to the Maven repository
adyen.repo.xapikey=<Your SDK Download API key>
# Credentials to the Adyen TEST environment
environment.merchantAccount=<Your merchant account>
environment.apiKey=<Your Merchant Account API key>
```

## Documentation
See our documentation on [docs.adyen.com](https://docs.adyen.com/point-of-sale/mobile-android/).
See SDK API reference on [GitHub](https://adyen.github.io/adyen-pos-mobile-android).

## Known Issues
* PIN Entry can occasionally fail in some circumstances when the android device running the SDK is set to a language other than English or when using certain currencies. This issue exists on all releases of the SDK.

## Support
If you have a feature request, or spotted a bug or a technical problem, please contact our support team.

## License
MIT license. For more information, see the LICENSE file.
