package com.livechatinc.inappchat;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by szymonjarosz on 18/07/2017.
 */

class ChatWindowJsInterface {
    private final ChatWindowView view;
    public static final String BRIDGE_OBJECT_NAME= "androidMobileWidget";
    private static final String KEY_MESSAGE_TYPE = "messageType";
    private static final String TYPE_UI_READY = "uiReady";
    private static final String TYPE_HIDE_CHAT_WINDOW = "hideChatWindow";
    private static final String TYPE_NEW_MESSAGE = "newMessage";

    public ChatWindowJsInterface(ChatWindowView view) {
        this.view = view;
    }

    @JavascriptInterface
    public void postMessage(String messageJson) {
        Log.i("Interface", "postMessage: " + messageJson);
        try {
            JSONObject jsonObject = new JSONObject(messageJson);
            if(jsonObject != null && jsonObject.has(KEY_MESSAGE_TYPE)){
                dispatchMessage(jsonObject.getString(KEY_MESSAGE_TYPE), jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void dispatchMessage(String messageType, JSONObject jsonObject) {
        switch (messageType){
            case TYPE_HIDE_CHAT_WINDOW:
                view.onHideChatWindow();
                break;
            case TYPE_UI_READY:
                view.onUiReady();
                break;
            case TYPE_NEW_MESSAGE:
                //TODO: json parsing here
                break;

        }
    }
}
