chat-window-android
===============

Embedding mobile chat window in Android application for

**LiveChat:** https://developers.livechatinc.com/mobile/android/ and

**Chat.io:** https://chat.io/docs/

# Installation

https://jitpack.io/#livechat/chat-window-android

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
    compile 'com.github.livechat:chat-window-android:v2.0.2'
}
```

Your application will need a permission to use the Internet. Add the following line to your **AndroidManifest.xml**:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```
<div class="clear"></div>

If you want to allow users to upload files from their external storage using chat window, a permission is also needed:

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```
<div class="clear"></div>

# Usage

There are couple ways you can use this library. To be able to use all the features we recommend you add chat window as a view, either by using static method which adds view to your activity, or as an embedded view in your xml. As long as ChatWindowView is initilized, you will get events when new message comes in.

First, you need to configure your chat window

## Configuration

Simply use ChatWindowConfiguration.java constructor. Note that licence number is mandatory.

```configuration = new ChatWindowConfiguration("your_licence_number", "group_id", "Visitor name", "visitor@email.com", customParamsMap);```

## Chat Window View

There are two recommended ways to use ChatWindow.
* Full screen ChatWindow added to the root of your activity, and
* XML embedded ChatWindow to controll placement and size

### Full Screen Window

All you need to do is to create, attach and initialize chat window. Something in the lines of:

```java
public void startFullScreenChat() {
    if (fullScreenChatWindow == null) {
        fullScreenChatWindow = ChatWindowView.createAndAttachChatWindowInstance(getActivity());
        fullScreenChatWindow.setUpWindow(configuration);
        fullScreenChatWindow.setUpListener(this);
        fullScreenChatWindow.initialize();
    }
    fullScreenChatWindow.showChatWindow();
}
```

### XML Embedded View

If you like to control the place and size of the ChatWindowView, you might want to add it to your app either by inlucidng a view in XML
```xml
<com.livechatinc.inappchat.ChatWindowView
    android:id="@+id/embedded_chat_window"
    android:layout_width="match_parent"
    android:layout_height="400dp"/>
```
or inflating the view directly
```java
ChatWindowView chatWindowView = new ChatWindowView(MainActivity.this);
```

and then initializing ChatWindow like with full screen window approach:
```java
public void startEmmbeddedChat(View view) {
    if (!emmbeddedChatWindow.isInitialized()) {
        emmbeddedChatWindow.setUpWindow(configuration);
        emmbeddedChatWindow.setUpListener(this);
        emmbeddedChatWindow.initialize();
    }
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
* handle a case, when user wants to attach file in ChatWindow
* get notified if new message arrived in chat. This gets handy if you want to show some kind of badge for a user to read new message.
* react on visibility changes (user can hide the view on its own)
* handle user selected links in a custom way

### File sharing

To provide your users capablity to send files, you need to set ChatWindowEventsListener on your ChatWindowView and give opportunity to the view to handle activity result, i.e.
```java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (fullChatWindow != null) fullChatWindow.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
}
```

### Handling URL's

You can disable chat widget's default behavior when user selects link by implementing `handleUri` method from ChatWindowEventsListener.
```java
@Override
public boolean handleUri(Uri uri) {
	// Handle uri here...
	return true; // Return true to disable default behavior.
}
````

## Alternative usage with limited capabilities

If you want you don't want users to be notified when user gets new message in hidden Chat, you might want to use provided activity or fragment

If you do not want the chat window to reload its content every time device orientation changes, add this line to your Activity in the manifest:

```java
android:configChanges="orientation|screenSize"
```
<div class="clear"></div>

The chat window will handle the orientation change by itself.

## Example usage

There are two ways to open the chat window – using Activity or Fragment.

### Using Activity

In order to open a chat window in new Activity, you need to declare **ChatWindowActivity** in your manifest. Add the following line to **AndroidManifest.xml**, between `<application></application>` tags:

```xml
<activity android:name="com.livechatinc.inappchat.ChatWindowActivity" android:configChanges="orientation|screenSize" />
```

<div class="clear"></div>

Finally, add the following code to your application, in a place where you want to open the chat window (e.g. button listener). You need to provide a Context (your Activity or Application object), your LiveChat or Chat.io license number (taken from the your app: [LiveChat](https://my.livechatinc.com/settings/code) or [Chat.io](https://app.chat.io/settings/installation) and, optionally, an ID of a group:

```java
Intent intent = new Intent(context, com.livechatinc.inappchat.ChatWindowActivity.class);
intent.putExtra(com.livechatinc.inappchat.ChatWindowActivity.KEY_GROUP_ID, "your_group_id");
intent.putExtra(com.livechatinc.inappchat.ChatWindowActivity.KEY_LICENSE_NUMBER, "your_license_number");
context.startActivity(intent);
```

<div class="clear"></div>

It’s also possibile to automatically login to chat window by providing visitor’s name and email. You need to disable [pre-chat survey](https://my.livechatinc.com/settings/pre-chat-survey) in the web application and add the following lines to the previous code:

```java
intent.putExtra(com.livechatinc.inappchat.ChatWindowActivity.KEY_VISITOR_NAME, "your_name");
intent.putExtra(com.livechatinc.inappchat.ChatWindowActivity.KEY_VISITOR_EMAIL, "your_email");
```

### Using Fragment

In order to open chat window in new Fragment, you need to add the following code to your application, in a place where you want to open the chat window (e.g. button listener). You also need to provide your LiveChat or Chat.io license number and group ID:

```java
getFragmentManager()
   .beginTransaction()
   .replace(R.id.frame_layout, ChatWindowFragment.newInstance("your_license_number", "your_group_id"), "chat_fragment")
   .addToBackStack("chat_fragment")
   .commit();
```

<div class="clear"></div>

Method `ChatWindowFragment.newInstance()` returns chat window Fragment.

<div class="clear"></div>

It’s also possible to automatically login to chat window by providing visitor’s name and email. You need to disable [pre-chat survey](https://my.livechatinc.com/settings/pre-chat-survey) in web application and use different `newInstance()` method:

```java
getFragmentManager()
   .beginTransaction()
   .replace(R.id.frame_layout, ChatWindowFragment.newInstance("your_license_number", "your_group_id", “visitor _name”, “visitor _email”), "chat_fragment")
   .addToBackStack("chat_fragment")
   .commit();
```

# Localisation

You can change or localize error messages, by defining your own string resources with following id's
```xml
<string name="failed_to_load_chat">Couldn\'t load chat.</string>
<string name="cant_share_files">File sharing is not configured for this app</string>
<string name="reload_chat">Reload</string>
```
