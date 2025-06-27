LiveChat SDK for Android
===============

[![Release](https://jitpack.io/v/livechat/chat-window-android.svg)](https://jitpack.io/#livechat/chat-window-android)

Embedding mobile chat window in Android application for

**LiveChat:** https://developers.livechat.com/docs/getting-started/installing-livechat/android-widget/

# Installation

[![Release](https://jitpack.io/v/livechat/chat-window-android.svg)](https://jitpack.io/#livechat/chat-window-android)

To get a project into your build:

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
     repositories {
         ...
         maven { url 'https://jitpack.io' }
     }
 }
```
Step 2. Add the dependency
```
dependencies {
    implementation 'com.github.livechat:chat-window-android:v2.5.0'
}
```

Your application will need a permission to use the Internet. Add the following line to your **AndroidManifest.xml**:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```
<div class="clear"></div>

# Usage

There are couple ways you can use this library. To be able to use all the features we recommend you add chat window as a view, either by using static method which adds view to your activity, or as an embedded view in your xml. As long as ChatWindowView is initilized, you will get events when new message comes in.

First, you need to configure your chat window

## Configuration

Simply use ChatWindowConfiguration.java constructor. Note that licence number is mandatory.

```java
configuration = new ChatWindowConfiguration(
    "your_licence_number",
    "group_id",         // optional
    "Visitor name",     // optional
    "visitor@email.com",// optional
    customParamsMap     // optional
);
```

You could also use `new ChatWindowConfiguration.Builder()`.

## Chat Window View

There are two recommended ways to use ChatWindow.
* Full screen ChatWindow added to the root of your activity, and
* XML embedded ChatWindow to controll placement and size

### Full Screen Window

All you need to do is to create, attach and initialize chat window. Something in the lines of:

```java
public void startFullScreenChat() {
    if (fullScreenChatWindow == null) {
        fullScreenChatWindow = ChatWindowUtils.createAndAttachChatWindowInstance(getActivity());
        fullScreenChatWindow.setEventsListener(this);
        fullScreenChatWindow.init(configuration);
    }
    fullScreenChatWindow.showChatWindow();
}
```

### XML Embedded View

If you like to control the place and size of the ChatWindowView, you might want to add it to your app either by inlucidng a view in XML
```xml
<com.livechatinc.inappchat.ChatWindowViewImpl
    android:id="@+id/embedded_chat_window"
    android:layout_width="match_parent"
    android:layout_height="400dp"/>
```
or inflating the view directly
```java
ChatWindowViewImpl chatWindowView = new ChatWindowViewImpl(MainActivity.this);
```

and then initializing ChatWindow like with full screen window approach:
```java
public void startEmmbeddedChat(View view) {
    emmbeddedChatWindow.setEventsListener(this);
    emmbeddedChatWindow.init(configuration);
    // ...
    emmbeddedChatWindow.showChatWindow();
}
```

## Navigation

Depending on your use case you might want to hide ChatWindow if user hits back button.
You can use our onBackPressed() function which hides the view if its visible and returns true.
In your activity/fragment add the following:
```java
@Override
public boolean onBackPressed() {
    return fullScreenChatWindow != null && fullScreenChatWindow.onBackPressed();
}
```

## ChatWindowEventsListener

This listener gives you opportunity to:
* get notified if new message arrived in chat. This gets handy if you want to show some kind of badge for a user to read new message.
* react on visibility changes (user can hide the view on its own)
* handle user selected links in a custom way
* react and handle errors coming from chat window
* allow users to use SnapCall integration
* get notified if user device can't handle file picker activity Intent

### File sharing

To provide your users capability to send files, you need to set it up through `supportFileSharing` on your `ChatWindowView`.
In case of operating system not able to handle Intent to pick files, you can handle it via `onFilePickerActivityNotFound` callback in `ChatWindowEventsListener`.

### Handling URL's

You can disable chat widget's default behavior when user selects link by implementing `handleUri` method from ChatWindowEventsListener.
```java
@Override
public boolean handleUri(Uri uri) {
	// Handle uri here...
	return true; // Return true to disable default behavior.
}
````

### Error handling

Errors are reported through the `onError` callback. Return `true` if you don't want the ChatWindow to display an error view with a reload button. The ChatWindow handles connection issues and can reconnect automatically.

We aim to report only critical errors. If you notice an important error being ignored or a non-critical error being reported, please inform us.

Refer to `ChatWindowEventsListener.onError` and `ChatWindowErrorType` for more details.

### Clear chat session

After your user signs out of the app, you might want to clear the chat session.
You can do that by invoking static method on `ChatWindowUtils.clearSession()` from anywhere in the app.
In case your `ChatWindowView` is attached in course of the log out flow, you also going to need to reload it by calling
`chatWindow.reload()` after clearSession code. See [FullScreenWindowActivityExample.java](https://github.com/livechat/chat-window-android/blob/master/app/src/main/java/com/livechatinc/livechatwidgetexample/FullScreenWindowActivityExample.java)

## Alternative usage with limited capabilities

If you don't need to be notified when user gets new message in a hidden ChatWindow, you might want to use `ChatWindowActivity` or `ChatWindowFragment`

<div class="clear"></div>

### Using Activity

In order to open a chat window in new Activity, you need to declare `ChatWindowActivity` in your manifest. Add the following line to `AndroidManifest.xml`, between `<application></application>` tags:

```xml
<activity 
    android:name="com.livechatinc.inappchat.ChatWindowActivity" 
    android:configChanges="orientation|screenSize" 
    android:exported="false" 
/>
```

<div class="clear"></div>

Finally, add the following code to your application, in a place where you want to open the chat window (e.g. button listener). You need to provide a Context (your Activity or Application object) and your LiveChat license number:

```java
Intent intent = new Intent(context, com.livechatinc.inappchat.ChatWindowActivity.class);
Bundle config = new ChatWindowConfiguration.Builder()
    .setLicenceNumber("<your_license_number>")
    .build();

intent.putExtra(ChatWindowConfiguration.KEY_CHAT_WINDOW_CONFIG, windowConfig);
startActivity(intent);
```

<div class="clear"></div>

It’s also possibile to automatically login to chat window by providing visitor’s name and email and disabling [pre-chat survey](https://my.livechatinc.com/settings/pre-chat-survey).
### Using Fragment

In order to open chat window in new Fragment, you need to add the following code to your application, in a place where you want to open the chat window (e.g. button listener). You also need to provide your LiveChat license number:

```java
getSupportFragmentManager()
   .beginTransaction()
   .replace(
        R.id.frame_layout, 
        ChatWindowFragment.newInstance(
            "your_license_number", 
            "your_group_id",// optional
            "visitor_name", // optional
            "visitor_email",// optional     
        ), 
        "chat_fragment"
    )
   .addToBackStack("chat_fragment")
   .commit();
```

<div class="clear"></div>

It’s also possible to automatically login to chat window by providing visitor’s name and email to the Fragment and disabling [pre-chat survey](https://my.livechatinc.com/settings/pre-chat-survey).

# Localisation

You can change or localize error messages, by defining your own string resources with following id's
```xml
<string name="failed_to_load_chat">Couldn\'t load chat.</string>
<string name="cant_share_files">File sharing is not configured for this app</string>
<string name="reload_chat">Reload</string>
```

### Migration details

Since version 2.4.0, migration details are listed in CHANGELOG.md.

### Migrating to version >= 2.2.0
* ChatWindowView is now interface that can be casted to View
* `setUpWindow(configuration);` is replaced by `setConfiguration(configuration);`
* `setUpListener(listener)` is replaced by `setEventsListener(listener)`
* `ChatWindowView.clearSession()` is moved to `ChatWindowUtils.clearSession(Context)`
* `ChatWindowView.createAndAttachChatWindowInstance(Activity)` is moved to `ChatWindowUtils.createAndAttachChatWindowInstance(getActivity())`

### Migrating to versions >=2.3.x
* You no longer need to specify `android.permission.READ_EXTERNAL_STORAGE` permission in your AndroidManifest.xml 

# SnapCall integration

SnapCall integration requires AUDIO and VIDEO permissions. In order to allow your users to use SnapCall integration you need to:
1. Set up your ChatWindowView Event listener, check [ChatWindowEventsListener](#ChatWindowEventsListener)
2. Add following permissions to you app `AndroidManifest.xml` file
```xml
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.CAMERA" />

```
3. Override `void onRequestAudioPermissions(String[] permissions, int requestCode)` to ask user for permissions, like so:
```java
@Override
public void onRequestAudioPermissions(String[] permissions, int requestCode) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.requestPermissions(permissions, requestCode);
    }
}
```
4. Override your activity `void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)` to pass result to `ChatWindowView`
```java
if (!chatWindow.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
}
```

For reference, check `FullScreenWindowActivityExample.java`
