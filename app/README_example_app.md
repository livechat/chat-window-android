LiveChat Android SDK example app
===============

Example app for LiveChat Android SDK demonstrating LiveChat SDK integration into
an Android application, showcasing different approaches

### Configuration

In `settings.gradle` add example app module -> `include ':ChatWidget', ':app'`

You can change license in `BaseApplication.kt` file for prod environment or with `local.properties` for more options

In project root add `local.properties` file with the following content:

```
license="<YOUR_LICENSE>"
chatUrl="https://<prod_or_test_env>/any/url/but/must/contain{%license%}and{%group%}placeholders"
clientId="<YOUR_CLIENT_ID_FROM_DEVELOPERS_CONSOLE>" # Used for CIP
licenseId="<YOUR_LICENSE_ID_FROM_DEVELOPERS_CONSOLE>" # Used for CIP
```

### Connecting to locally hosted environment

* Add `android:usesCleartextTraffic="true"` to application tag in `app/src/main/AndroidManifest.xml`
* (optional) Add http://10.0.2.2:3000 to Redirect URI whitelist in Developers console for App Authorization - more about emulator networking here
* Create a link to your dev machine local ost: http://10.0.2.2:3000/iframe.html?group=0&license_id=100370559&webview_widget=1 put it in `local.properties`


### Inspecting webView in chrome://inspect

You can inspect the webView in Chrome DevTools once chat is loaded. But if you want to have it running before, you can do the following:
* You must use `LiveChatViewLifecycleScope.APP` scope
* Load empty document `webView.loadUrl("data:text/html,<html></html>")` at the end of `LiveChatView.configureWebView()`
* Initiate LiveChatView at the end of `HomeFragment.onViewCreated()` method with `LiveChat.getInstance().getLiveChatView()`


### Build issues

In case you see issue during initial Sync or Build, you might want to try to:

* `cmd + ,` → search Gradle → select jbr-17 for Gradle JDK
* `cmd + ;` -> Modules -> select app module -> set Source Compatibility and Target Compatibility to Java 1.8. Same for library module


### Run on real device

Connecting with local widget might be tricky, but test and prod environment config will work normally

* enable developer options on android device
* connect via usb
* select your device - same place where you select Android Emulator
