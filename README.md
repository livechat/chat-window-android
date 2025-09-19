LiveChat SDK for Android
===============

[![Release](https://jitpack.io/v/livechat/chat-window-android.svg)](https://jitpack.io/#livechat/chat-window-android)

An Android SDK for integrating LiveChat functionality into your mobile application.

**LiveChat:** https://developers.livechat.com/docs/getting-started/installing-livechat/android-widget/

# Table of contents

1.  [Getting started](#getting-started)
    1. [Requirements](#requirements)
    1. [Installation](#installation)
    1. [Initialization](#initialization)
    1. [Display the chat window](#display-the-chat-window)
1. [Customer information](#customer-information)
1. [Unread message counter](#unread-message-counter)
1. [UI Customization](#ui-customization)
   1. [Error view](#error-view)
   1. [Activity](#activity)
1. [Clearing chat session](#clearing-chat-session)
1. [Link handling](#link-handling)
1. [Troubleshooting](#troubleshooting)
   1. [Error handling](#error-handling)
   1. [Logger](#logger)
   1. [Missing file picker activity](#missing-file-picker-activity)
1. [Advanced usage](#advanced-usage)
   1. [LiveChatView lifecycle modes](#livechatview-lifecycle-scope)
   1. [Embedding LiveChatView](#embed-livechatview-in-your-layout)
1. [Migrating from v2.5.0 to 3.0.0](#migrating-from-v250-to-300)



# Getting started

Add real-time customer support to your Android application with LiveChat's SDK. This guide covers installation, basic setup, and advanced features.

> **Note:** 💡 The SDK is now Kotlin-based and uses the new package `com.livechatinc.chatsdk`. See migration notes if you are upgrading from v2.x.x.

## Requirements

The Android SDK is compatible with:
- `Android 5.0 (API level 21) or higher`
- `Java 8 or higher`

## Installation

To install the SDK, first add the JitPack repository to your root `build.gradle`:
```kotlin
allprojects {
   repositories {
      maven { url 'https://jitpack.io' }
   }
}
```

Next, add dependency to your app's `build.gradle`:

```kotlin
dependencies {
    implementation 'com.github.livechat:chat-window-android:3.0.1'
}
```

Then, add Internet permission to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Initialization

You can initialize the SDK in the `onCreate()` method of your [`Application`](https://developer.android.com/reference/android/app/Application) instance:

```kotlin
LiveChat.initialize("<LICENSE>", this)
```

## Display the chat window

The function below allows you to display a chat to a customer:

```kotlin
LiveChat.getInstance().show()
```
That's it! Your customers can start chatting with you now.

# Customer information

You can pre-fill the [pre-chat form](https://my.livechatinc.com/settings/pre-chat-form) fields with customer information to provide your agents with more details about the customer. All information is optional.

The group ID defaults to `0` if not provided.

```kotlin
LiveChat.getInstance().setCustomerInfo(
            "Joe", // Name
            "joe@mail.com", // Email
            "0", // Group ID, defaults to "0"
            Collections.singletonMap("internalCustomerId", "ABC123") // Any additional custom parameters
        )
```

> **Note:** You should call the `setCustomerInfo()` before `LiveChat.getInstance().show()`. To update customer properties when the chat has already loaded, recreate it with `LiveChat.getInstance().destroyLiveChatView()` and `LiveChat.getInstance().getLiveChatView()`.

# Unread message counter

To get notified about new messages in the chat, use [`com.livechatinc.chatsdk.src.domain.listeners.NewMessageListener`](https://github.com/livechat/chat-window-android/blob/master/chat-sdk/src/main/java/com/livechatinc/chatsdk/src/domain/interfaces/NewMessageListener.kt).

Set it wherever you want to react to a new message, for example, to increase the badge count.

```kotlin
LiveChat.getInstance().newMessageListener =
   NewMessageListener { message, isChatShown ->
      // Handle new message
   }
```

# UI customization

While the chat appearance and language are managed through the application settings, you can customize the error view when chat cannot be loaded.

## Error view

You can localize and change the text displayed in the error view by overriding string resources in your app's `strings.xml`. All strings can be found in the [GitHub repository](https://github.com/livechat/chat-window-android/blob/master/chat-sdk/src/main/res/values/strings.xml).

You can also entirely override the default error view by including `live_chat_error_layout.xml` in your app's layout resources.

> **Note:** Your custom error view must contain a `View` with `live_chat_error_button` id when using `LiveChat.getInstance().show()`.

## Activity

By default, activity will follow your activity's theme. To change this configuration, you can override the activity declaration in your app's `AndroidManifest.xml` file.

# Clearing chat session

The chat window uses WebView's cookies to store the session. To clear the chat session, call:

```kotlin
LiveChat.getInstance().signOutCustomer()
```

# Link handling

By default, links in the chat are opened in the customer's default browser. If you want to intercept the link and handle it in your app, provide your `UrlHandler`.

```kotlin
LiveChat.getInstance().urlHandler = 
   UrlHandler { url ->
       // Handle the URL in your app and return true if handled 
       return@UrlHandler false
   }
```

# Troubleshooting

## Error handling

You can set an error listener to monitor and handle issues related to loading the chat view. This allows you to capture and respond to errors in a centralized way. Common use cases include reporting errors to analytics platforms or implementing custom error-handling logic.

```kotlin
LiveChat.getInstance().errorListener = ErrorListener { error ->
   // Handle the error
}
```

## Logger

You can configure the logging level to help with debugging and troubleshooting. Set the desired level before initializing LiveChat:

```kotlin
Logger.setLogLevel(Logger.LogLevel.VERBOSE);
```

Refer to the [Logger](https://github.com/livechat/chat-window-android/blob/master/chat-sdk/src/main/java/com/livechatinc/chatsdk/src/utils/Logger.kt) for all available log levels.

> **Note**: Network calls require at least the `INFO` level. `DEBUG` and `VERBOSE` levels are more detailed.

## Missing file picker activity

If you want to react or track instances where the activity for file picking on the user device is not found, you can set a listener for this event:

```kotlin
LiveChat.getInstance().filePickerNotFoundListener = FilePickerActivityNotFoundListener {
   // Handle the case when file picker activity is not found
}
```

# Advanced usage

The following features give you more control over the SDK integration. Note that they require additional implementation steps. If you decide to use any of these, please let us know about your use case — we would love to hear about it!

## LiveChatView lifecycle scope

By default, after the `LiveChat.getInstance().show()` view is initialized, it's kept in the application memory. This allows you to react to new messages, for example, display a [counter for unread messages](#unread-message-counter). To automatically free up memory when the chat is no longer visible, you can use `LiveChatViewLifecycleScope` to control its lifecycle.

You should specify the scope when initializing the SDK:

```kotlin
LiveChat.initialize("<LICENSE>", this, lifecycleScope = LiveChatViewLifecycleScope.ACTIVITY)
```

> **Note:** With the `ACTIVITY` scope, the `NewMessageListener` only works while the chat is visible.

## Embed LiveChatView in your layout

You can embed the `LiveChatView` directly in your layout for more control over the chat window and its placement, instead of using `LiveChat.getInstance().show()`. For full implementation details, refer to the [LiveChatActivity](https://github.com/livechat/chat-window-android/blob/master/chat-sdk/src/main/java/com/livechatinc/chatsdk/src/presentation/LiveChatActivity.kt).

Here is a short overview of the steps to embed the `LiveChatView` in your layout:

### 1. Add LiveChatView to your layout

Begin with adding `<com.livechatinc.chatsdk.src.presentation.LiveChatView />` to your layout XML file.

### 2. Provide activity or fragment context

During `onCreate` of your `Activity` or `Fragment`, call:

```kotlin
liveChatView.attachTo(this)
```

This is required to properly handle the view's lifecycle, support file sharing, and launch links in the default external browser.

### 3. React to visibility events

Provide `LiveChatView.InitListener` when initializing the view:

```kotlin
liveChatView.init(initCallbackListener)
```

# Migrating from v2.5.0 to 3.0.0

## Key Changes
* The SDK is now Kotlin-based.
* The API is now streamlined and uses the `LiveChat` singleton.
* The package changed to `com.livechatinc.chatsdk`.
* Updated API for initializing and configuring the chat
* Edge-to-edge display support
* Enhanced error handling

## Steps

### Update your dependency

```kotlin
dependencies {
    implementation 'com.github.livechat:chat-window-android:3.0.0'
}
```

> Note: With version 3 we no longer use "v" prefix

### Update configuration 
The new API uses LiveChat singleton instead of ChatWindowConfiguration and it's split into two parts: [initialization](#initialization) and [customer information setup](#customer-information).

Update [activity declaration](#activity) if needed

### Update event listeners

The old `ChatWindowEventsListener` has removed. Some of the callbacks are no longer needed, the rest has been split into individual callbacks
* `onWindowInitialized()` -> removed for [recommended integration](#display-the-chat-window). If you're embedding the view directly see [Embed LiveChatView in your layout](#embed-livechatview-in-your-layout) for details
* `onChatWindowVisibilityChanged` -> removed [recommended integration](#display-the-chat-window). If you're embedding the view directly see [Embed LiveChatView in your layout](#embed-livechatview-in-your-layout) for details
* `onNewMessage` -> replaced with [NewMessageListener](#unread-message-counter)
* `onRequestAudioPermissions` -> removed
* `onError` -> replaced with [ErrorListener](#error-handling)
* `handleUri` -> replaced with [UriHandler](#link-handling)
* `onFilePickerActivityNotFound` -> [FileChooserActivityNotFoundListener](#missing-file-picker-activity)

For a complete example of implementation, please refer to the example app included in the repository.

### v2.x.x

v2 of the library is still available on JitPack. You can find documentation by selecting v2.x.x tag in the repository
