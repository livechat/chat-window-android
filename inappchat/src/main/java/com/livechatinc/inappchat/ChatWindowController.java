package com.livechatinc.inappchat;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.livechatinc.inappchat.models.NewMessageModel;
import com.livechatinc.inappchat.src.internal.ChatWindowViewInternal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

class ChatWindowController {

    ChatWindowController(ChatWindowViewInternal chatWindowView, RequestQueue networkQueue) {
        this.chatWindowView = chatWindowView;
        this.networkQueue = networkQueue;
    }

    final ChatWindowViewInternal chatWindowView;
    final RequestQueue networkQueue;

    final String TAG = ChatWindowController.class.getSimpleName();

    private ChatWindowConfiguration config;
    protected ChatWindowEventsListener eventsListener;
    protected boolean chatUiReady = false;

    protected void setEventsListener(ChatWindowEventsListener eventsListener) {
        this.eventsListener = eventsListener;
    }

    protected boolean setConfig(ChatWindowConfiguration config) {
        final boolean isEqualConfig = this.config != null && this.config.equals(config);
        this.config = config;

        return !isEqualConfig;
    }


    protected void init() {
        checkConfiguration();
        JsonObjectRequest initializationRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://cdn.livechatinc.com/app/mobile/urls.json",
                null,
                this::onWindowInitialized,
                this::onWindowInitializationError
        );
        networkQueue.add(initializationRequest);
    }


    private void checkConfiguration() {
        if (config == null) {
            throw new IllegalStateException("Config must be provided before initialization");
        }
    }

    protected void reinitialize() {
        chatWindowView.showProgress();

        chatUiReady = false;
        init();
    }

    private void onWindowInitialized(JSONObject response) {
        Log.d(TAG, "Response: " + response);
        String chatUrl = constructChatUrl(response);
        Log.d(TAG, "constructed url: " + chatUrl);
        if (chatUrl != null) {
            chatWindowView.loadUrl(chatUrl);
        }
        if (eventsListener != null) {
            eventsListener.onWindowInitialized();
        }
    }

    private String constructChatUrl(JSONObject jsonResponse) {
        String chatUrl = null;
        try {
            chatUrl = jsonResponse.getString("chat_url");
            chatUrl = config.addParamsToChatWindowUrl(chatUrl);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing chat url from response: " + e.getMessage(), e);
        }

        return chatUrl;
    }

    private void onWindowInitializationError(VolleyError error) {
        Log.d(TAG, "Error response: " + error);
        final int errorCode = error.networkResponse != null ? error.networkResponse.statusCode : -1;
        final boolean errorHandled = eventsListener != null && eventsListener.onError(ChatWindowErrorType.InitialConfiguration, errorCode, error.getMessage());

        onErrorDetected(errorHandled, ChatWindowErrorType.InitialConfiguration, errorCode, error.getMessage());
    }

    protected void onErrorDetected(boolean errorHandled, ChatWindowErrorType errorType, int errorCode, String errorDescription) {
        chatWindowView.hideProgressBar();
        if (!errorHandled) {
            if (chatUiReady && errorType == ChatWindowErrorType.WebViewClient && errorCode == -2) {
                //Internet connection error. Connection issues handled in the chat window
                return;
            }
            chatWindowView.showErrorView();
        }
    }

    public void onPageLoaded() {
        chatWindowView.showWebView();
    }

    public boolean handleUri(Uri uri, String originalUrl) {
        String uriString = uri.toString();
        Log.i(TAG, "handle url: " + uriString);

        if (uriString.equals(originalUrl) || isSecureLivechatIncDomain(uri.getHost())) {
            return false;
        } else {
            if (eventsListener != null && eventsListener.handleUri(uri)) {

            } else {
                chatWindowView.launchExternalBrowser(uri);
            }

            return true;
        }
    }

    private boolean isSecureLivechatIncDomain(String host) {
        return host != null && Pattern.compile("(secure-?(lc|dal|fra|)\\.(livechat|livechatinc)\\.com)").matcher(host).find();
    }

    // JS Interface

    protected void onHideChatWindow() {
        chatWindowView.runOnMainThread(chatWindowView::hideChatWindow);
    }

    protected void onUiReady() {
        chatUiReady = true;
        chatWindowView.runOnMainThread(chatWindowView::hideProgressBar);
    }

    protected void onNewMessageReceived(final NewMessageModel newMessageModel) {
        if (eventsListener != null) {
            chatWindowView.runOnMainThread(() -> eventsListener.onNewMessage(newMessageModel, chatWindowView.isShown()));
        }
    }
}
