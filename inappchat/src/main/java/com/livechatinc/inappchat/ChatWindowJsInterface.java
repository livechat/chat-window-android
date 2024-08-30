package com.livechatinc.inappchat;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.GsonBuilder;
import com.livechatinc.inappchat.models.NewMessageModel;

import org.json.JSONException;
import org.json.JSONObject;

class ChatWindowJsInterface {
    public ChatWindowJsInterface(ChatWindowController controller) {
        this.controller = controller;
    }

    private final ChatWindowController controller;

    public static final String BRIDGE_OBJECT_NAME = "androidMobileWidget";
    private static final String KEY_MESSAGE_TYPE = "messageType";
    private static final String TYPE_UI_READY = "uiReady";
    private static final String TYPE_HIDE_CHAT_WINDOW = "hideChatWindow";
    private static final String TYPE_NEW_MESSAGE = "newMessage";
    private static final String TAG = ChatWindowJsInterface.class.getSimpleName();

    @JavascriptInterface
    public void postMessage(String messageJson) {
        Log.d(TAG, "postMessage: " + messageJson);
        try {
            JSONObject jsonObject = new JSONObject(messageJson);
            if (jsonObject != null && jsonObject.has(KEY_MESSAGE_TYPE)) {
                dispatchMessage(jsonObject.getString(KEY_MESSAGE_TYPE), messageJson);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error serializing js message: " + e.getMessage(), e);
        }
    }

    private void dispatchMessage(String messageType, String json) {
        switch (messageType) {
            case TYPE_HIDE_CHAT_WINDOW:
                controller.onHideChatWindow();
                break;
            case TYPE_UI_READY:
                controller.onUiReady();
                break;
            case TYPE_NEW_MESSAGE:
                controller.onNewMessageReceived(new GsonBuilder().create().fromJson(json, NewMessageModel.class));
                break;

        }
    }
}
