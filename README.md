LiveChat SDK for Android
===============

[![Release](https://jitpack.io/v/livechat/chat-window-android.svg)](https://jitpack.io/#livechat/chat-window-android)

A Android SDK for integrating LiveChat functionality into your mobile application.

**LiveChat:** https://developers.livechat.com/docs/getting-started/installing-livechat/android-widget/

## Table of contents

1.  [Getting started](#getting-started)
    1. [Requirements](#requirements)
    1. [Usage](#usage)
        1. [Install SDK](#install-sdk)
        1. [Initialize](#initialize)
        1. [Show Chat](#show-chat)
1. [Customer information](#customer-information)
1. [Unread message counter](#unread-message-counter)
1. [UI Customization](#ui-customization)
   1. [Localizing text](#localizing-text)
   1. [Custom error view](#custom-error-view)
   1. [Customizing Activity](#customizing-activity)
1. [Clearing chat session](#clearing-chat-session)
1. [Handling links](#handling-links)
1. [Troubleshooting](#troubleshooting)
   1. [Reacting to errors](#reacting-to-errors)
   1. [Logger](#logger)
1. [Advanced usage](#advanced-usage)
   1. [LiveChatView lifecycle modes](#livechatview-lifecycle-modes)
   1. [Embedding LiveChatView](#embedding-livechatview)
1. [Legacy version](#legacy-version)



## Getting started

Add real-time customer support to your Android application with LiveChat's native SDK. This guide covers installation, basic setup, and advanced features.

### Requirements

LiveChat SDK is compatible:
- `Android 5.0 (API level 21) or higher`
- `Java 8 or higher`

### Usage

Follow these steps to integrate LiveChat into your Android application:

#### Install SDK:

Add the JitPack repository to your root `build.gradle`
```kotlin
allprojects {
   repositories {
      maven { url 'https://jitpack.io' }
   }
}
```

Add dependency to your app's `build.gradle`:
```
dependencies {
    implementation 'com.github.livechat:com.livechatinc.chatwidget:3.0.0'
}
```

Add Internet permission to AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

#### Initialize:

Initialize the SDK in the `onCreate()` method of your [`Application`](https://developer.android.com/reference/android/app/Application) instance

```kotlin
LiveChat.initialize("<LICENSE_NUMBER>", this)
```

#### Show Chat

Show the the chat to a customer

```kotlin
LiveChat.show()
```
That's it! Your customer can already start chatting with you.

## Customer information

Pre-fill the [pre-chat form](https://my.livechatinc.com/settings/pre-chat-form) and provide customer details to your agents by setting customer information.
All information is optional. Group ID defaults to 0 if not provided.

```kotlin
LiveChat.getInstance().setCustomerInfo(
            "Joe", // Name
            "joe@mail.com", // Email
            "0", // Group ID, defaults to "0"
            Collections.singletonMap("internalCustomerId", "ABC123") // Any additional custom parameters
        )
```

> **Note:** You must provide this information before calling `LiveChat.show()` 

## Unread message counter

Use [`com.livechatinc.chatwidget.src.listeners.NewMessageListener`](ChatWidget/src/main/java/com/livechatinc/chatwidget/src/listeners/NewMessageListener.kt) to get notified about new messages in the chat

Set it wherever you want to react on new message, like increase badge count

```kotlin
LiveChat.getInstance().setNewMessageListener()
```

## UI Customization

While chat appearance and language settings are managed through the Agent App, you can customize how errors are displayed to users

### Localizing text

You can localize and change the text displayed in the error view by overriding string resources in your app's `strings.xml`.
All strings can be found [here](https://github.com/livechat/chat-window-android/blob/master/ChatWidget/src/main/res/values/strings.xml)

### Custom error view

To completely change the error view, you can also override the default one by including `live_chat_error_layout.xml` in your app's layout resources.
> **Note:** Your custom view must contain a `View` with `live_chat_error_button` id

### Customizing Activity

Activity will follow your activity's theme. To change activity configuration you can just override the activity declaration in your app `AndroidManifest.xml`

## Clearing chat session

Chat window uses WebView's cookies to store the session. To clear the chat session, you can call:

```kotlin
ChatWidgetUtils.clearSession()
```

## Handling links

By default, links sent between your Agents and Customers are opened in the default browser. 
If you want to intercept the link and handle it in your app, provide your `UriHandler`

```kotlin
LiveChat.getInstance().setUrlHandler(object : UriHandler {
    override fun handleUri(uri: Uri): Boolean {
        // Return true if handled, false to open in default browser
        return false
    }
})
```

## Troubleshooting

### React to errors

To monitor and handle issues related to loading the chat view, you can set an error listener. This allows you to capture and respond to errors in a centralized way. 
Common use cases include reporting errors to analytics platforms or implementing custom error handling logic.

```kotlin
LiveChat.getInstance().setErrorListener(object : ErrorListener {
    override fun onError(error: Error) {
        // Handle the error
    }
})
```


### Logger

Configure the logging level to help with debugging and troubleshooting. Set the desired level before initializing LiveChat:
Refer to [Logger](https://github.com/livechat/chat-window-android/blob/master/ChatWidget/src/main/java/com/livechatinc/chatwidget/src/common/Logger.kt) for available log levels.

```kotlin
Logger.setLogLevel(Logger.LogLevel.VERBOSE);
```

> Note: Network calls require at least `INFO`. `DEBUG` and `VERBOSE` provide maximum level of detail

## Advanced usage

For more control over the SDK integration, consider these advanced options. Note that they require additional implementation steps:
If you need to use the following usage, please let us know about your use case. We would love to hear from you!

### LiveChatView lifecycle modes

By default, after `LiveChat.show()` the view is inflated and kept in memory. This allows reacting to new messages as described in [Unread message counter](#unread-message-counter).
If you don't need that feature and want to free up memory once chat is no longer visible, you can use `LiveChatViewLifecycleMode` to control the lifecycle of the view. 
You should specify the mode when initializing.

```kotlin
LiveChat.initialize("<LICENSE_NUMBER>", this, LiveChatViewLifecycleMode.WHEN_SHOWN)
```

> **Note:** Using WHEN_SHOWN mode will disable the `NewMessageListener` no longer works when chat is not visible.

### Embedding LiveChatView

You can directly embed `LiveChatView` in your layout, instead of showing it in activity (`LiveChat.show()`). This allows you to have more control over the chat window and its placement in your app.
There are additional steps and requirements you need to follow to take full advantage of chat window.
Generally you can refer to `LiveChatActivity` for the implementation details, but here is a quick overview of the steps you need to take.

#### Embedding in you layout

Add `<com.livechatinc.chatwidget.LiveChatView />` your layout XML file

#### Provide activity context

During `onCreate` of your Activity, provide activity context

```kotlin
liveChatView.setActivityContextOnCreate(this)
```

> **Note:** this is required to properly handle the lifecycle of the view, support file sharing and launch links in default external browser

#### React to visibility events

Provide `LiveChatViewInitListener` when initializing the view

```kotlin
liveChatView.init(initCallbackListener)
```

## Legacy version

Old versions of the library are still available on JitPack. You can find documentation in the [README_legacy.md](README_legacy.md) file
