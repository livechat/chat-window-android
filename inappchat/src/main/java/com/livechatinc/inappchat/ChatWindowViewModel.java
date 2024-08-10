package com.livechatinc.inappchat;

import static java.security.AccessController.getContext;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.livechatinc.inappchat.models.NewMessageModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

class ChatWindowViewModel extends ViewModel {

    ChatWindowViewModel(ChatWindowViewImpl chatWindowView, RequestQueue queue) {
        this.chatWindowView = chatWindowView;
        this.queue = queue;
    }

    final ChatWindowViewImpl chatWindowView;
    final RequestQueue queue;

    private ChatWindowConfiguration config;

    protected ChatWindowEventsListener eventsListener;
    protected boolean chatUiReady = false;

    public void setEventsListener(ChatWindowEventsListener eventsListener) {
        this.eventsListener = eventsListener;
    }

    public boolean setConfig(ChatWindowConfiguration config) {
        final boolean isEqualConfig = this.config != null && this.config.equals(config);
        this.config = config;
        return !isEqualConfig;
    }


    public void init() {
        checkConfiguration();
        JsonObjectRequest initializationRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://cdn.livechatinc.com/app/mobile/urls.json",
                null,
                this::onWindowInitialized,
                this::onWindowInitializationError
        );
        queue.add(initializationRequest);
    }

    public void reinitialize() {
        chatWindowView.showProgress();

        chatUiReady = false;
        init();
    }

    private void onWindowInitialized(JSONObject response) {
        Log.d(TAG, "Response: " + response);
        String chatUrl = constructChatUrl(response);
        Log.d(TAG, "constructed url: " + chatUrl);
        if (chatUrl != null && getContext() != null) {
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
            e.printStackTrace();
        }

        return chatUrl;
    }

    private void onWindowInitializationError(VolleyError error) {
        Log.d(TAG, "Error response: " + error);
        final int errorCode = error.networkResponse != null ? error.networkResponse.statusCode : -1;
        final boolean errorHandled = eventsListener != null && eventsListener.onError(ChatWindowErrorType.InitialConfiguration, errorCode, error.getMessage());
        if (getContext() != null) {
            onErrorDetected(errorHandled, ChatWindowErrorType.InitialConfiguration, errorCode, error.getMessage());
        }
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

    private void checkConfiguration() {
        if (config == null) {
            throw new IllegalStateException("Config must be provided before initialization");
        }
    }

    final String TAG = ChatWindowViewModel.class.getSimpleName();

    public void onHideChatWindow() {
        chatWindowView.onHideChatWindow();
    }

    public void onUiReady() {
        chatUiReady = true;
        chatWindowView.post(chatWindowView::hideProgressBar);
    }

    protected void onNewMessageReceived(final NewMessageModel newMessageModel) {
        if (eventsListener != null) {
            chatWindowView.post(() -> eventsListener.onNewMessage(newMessageModel, chatWindowView.isShown()));
        }
    }

    protected static boolean isSecureLivechatIncDomain(String host) {
        return host != null && Pattern.compile("(secure-?(lc|dal|fra|)\\.(livechat|livechatinc)\\.com)").matcher(host).find();
    }
}
