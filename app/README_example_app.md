LiveChat Android SDK example app
===============

Example app for LiveChat Android SDK demonstrating LiveChat SDK integration into
an Android application

## Setup

In `settings.gradle` add example app module 
```gradle
include ':ChatWidget', ':app'
```

Configure your license in one of two ways:
* For production: Edit the license in `BaseApplication.kt`
* For production or development: Use `local.properties` (see below)

### License and environment configuration

Create a `local.properties` file in the project root with:

```
license="<YOUR_LICENSE>"
chatUrl="https://<prod_or_test_env>/any/url/but/must/contain{%license%}and{%group%}placeholders"
clientId="<YOUR_CLIENT_ID_FROM_DEVELOPERS_CONSOLE>" # Used for CIP
licenseId="<YOUR_LICENSE_ID_FROM_DEVELOPERS_CONSOLE>" # Used for CIP
```

> **Note:** `chatUrl` must contain `{%license%}` and `{%group%}` placeholders

### Locally development environment

* Add `android:usesCleartextTraffic="true"` to application tag in `app/src/main/AndroidManifest.xml`
* Create a link to your dev machine local host in `local.properties`: 
http://10.0.2.2:3000/iframe.html?group=0&license_id=100370559&webview_widget=1 put it in
* (required for CIP) Add http://10.0.2.2:3000 to Redirect URI whitelist in Developers console for App
  Authorization - more about emulator networking [here](https://developer.android.com/studio/run/emulator-networking)

### Debugging

#### Inspecting WebView in Chrome DevTools

To inspect the WebView before chat is loaded:

* Use `LiveChatViewLifecycleScope.APP` scope
* Load empty document during webview configuration in `LiveChatView.configureWebView()` 
```kotlin
    private fun configureWebView() {
        ...

        webView.loadUrl("data:text/html,<html></html>")
    }
```
* Initialize LiveChatView at the end of `HomeFragment.onViewCreated()` 

```kotlin
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ...

        LiveChat.getInstance().getLiveChatView()
    }
```
* Navigate to `chrome://inspect` - you should see your WebView listed there once you reach current library home screen

### Build issues

If you encounter build issues during initial sync:

Set Gradle JDK:
* `cmd + ,` → search Gradle → select jbr-17 for Gradle JDK

Update Java compatibility:
* `cmd + ;` -> Modules -> select app module -> set Source Compatibility and Target Compatibility to
  Java 1.8. Same for library module

### Testing on Physical Devices

To run the app on a real device:

* enable [developer options](https://developer.android.com/studio/debug/dev-options) on android device
* connect via usb
* select your device - same place where you select Android Emulator

> Note: When testing on a physical device, local widget connections might be challenging, but test and production environment configurations should work fine
