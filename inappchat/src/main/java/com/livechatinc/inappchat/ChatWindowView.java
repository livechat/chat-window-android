package com.livechatinc.inappchat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.livechatinc.inappchat.models.NewMessageModel;

import java.io.File;

/**
 * Created by szymonjarosz on 19/07/2017.
 */

public class ChatWindowView extends FrameLayout implements IChatWindowView, View.OnClickListener {
    private WebView webView;
    private TextView statusText;
    private Button reloadButton;
    private ProgressBar progressBar;
    private WebView webViewPopup;
    private LoadWebViewContentTask loadWebViewContentTask;
    private ChatWindowEventsListener chatWindowListener;
    private static final int REQUEST_CODE_FILE_UPLOAD = 21354;

    private ValueCallback<Uri> mUriUploadCallback;
    private ValueCallback<Uri[]> mUriArrayUploadCallback;
    private ChatWindowConfiguration config;
    private boolean initialized;

    /*
        Creates an instance of ChatWindowView an attaches to the provided activity.
        ChatWindowView is hidden until it is initialized and shown.
     */
    public static ChatWindowView createAndAttachChatWindowInstance(@NonNull Activity activity) {
        final ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        ChatWindowView chatWindowView = (ChatWindowView) LayoutInflater.from(activity).inflate(R.layout.view_chat_window, contentView, false);
        contentView.addView(chatWindowView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        return chatWindowView;
    }

    public ChatWindowView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ChatWindowView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setFitsSystemWindows(true);
        setVisibility(GONE);
        LayoutInflater.from(context).inflate(R.layout.view_chat_window_internal, this, true);
        webView = (WebView) findViewById(R.id.chat_window_web_view);
        statusText = (TextView) findViewById(R.id.chat_window_status_text);
        progressBar = (ProgressBar) findViewById(R.id.chat_window_progress);
        reloadButton = (Button) findViewById(R.id.chat_window_button);
        reloadButton.setOnClickListener(this);

        if (Build.VERSION.RELEASE.matches("4\\.4(\\.[12])?")) {
            String userAgentString = webView.getSettings().getUserAgentString();
            webView.getSettings().setUserAgentString(userAgentString + " AndroidNoFilesharing");
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        webView.setFocusable(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        webView.setWebViewClient(new LCWebViewClient());
        webView.setWebChromeClient(new LCWebChromeClient());

        webView.requestFocus(View.FOCUS_DOWN);
        webView.setVisibility(GONE);

        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
        webView.addJavascriptInterface(new ChatWindowJsInterface(this), ChatWindowJsInterface.BRIDGE_OBJECT_NAME);
    }

    @Override
    public void onClick(View view) {
        loadWebViewContentTask.cancel(true);

        webView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.GONE);
        reloadButton.setVisibility(View.GONE);

        initialized = false;
        initialize();
    }

    public void setUpListener(ChatWindowEventsListener listener) {
        chatWindowListener = listener;
    }

    /*
    Checks the configuration and initializes ChatWindow, loading the view.
     */
    public void initialize() {
        checkConfiguration();
        initialized = true;
        loadWebViewContentTask = new LoadWebViewContentTask(webView, progressBar, statusText, reloadButton);
        loadWebViewContentTask.execute(config.getParams());
    }

    private void checkConfiguration() {
        if (config == null) {
            throw new IllegalStateException("Config must be provide before initialization");
        }
        if (initialized) {
            throw new IllegalStateException("Chat Window already initialized");
        }
    }

    public void onHideChatWindow() {
        post(new Runnable() {
            @Override
            public void run() {
                hideChatWindow();
            }
        });
    }

    @Override
    public void showChatWindow() {
        ChatWindowView.this.setVisibility(VISIBLE);
        if (chatWindowListener != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    chatWindowListener.onChatWindowVisibilityChanged(true);
                }
            });
        }
    }

    @Override
    public void hideChatWindow() {
        ChatWindowView.this.setVisibility(GONE);
        if (chatWindowListener != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    chatWindowListener.onChatWindowVisibilityChanged(false);
                }
            });
        }
    }

    public boolean onBackPressed() {
        if (ChatWindowView.this.isShown()) {
            onHideChatWindow();
            return true;
        }
        return false;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FILE_UPLOAD) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                receiveUploadedData(data);
            } else {
                resetAllUploadCallbacks();
            }
            return true;
        }
        return false;
    }

    private void receiveUploadedData(Intent data) {
        if (isUriArrayUpload()) {
            receiveUploadedUriArray(data);
        } else if (isVersionPreHoneycomb()) {
            receiveUploadedUriPreHoneycomb(data);
        } else {
            receiveUploadedUri(data);
        }
    }

    private boolean isUriArrayUpload() {
        return mUriArrayUploadCallback != null;
    }

    private boolean isVersionPreHoneycomb() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    public void setUpWindow(ChatWindowConfiguration configuration) {
        this.config = configuration;
    }

    public void onUiReady() {
        post(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
            }
        });
    }

    private void hideProgressBar() {
        progressBar.setVisibility(GONE);

    }

    public void onNewMessageReceived(final NewMessageModel newMessageModel) {
        if (chatWindowListener != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    chatWindowListener.onNewMessage(newMessageModel, isShown());
                }
            });
        }
    }

    class LCWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.startsWith("https://www.facebook.com/dialog/return/arbiter")) {
                if (webViewPopup != null) {
                    webViewPopup.setVisibility(GONE);
                    removeView(webViewPopup);
                    webViewPopup = null;
                }
            }

            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(GONE);
                    webView.setVisibility(GONE);
                    statusText.setVisibility(View.VISIBLE);
                }
            });

            super.onReceivedError(view, request, error);
            Log.e("ChatWindow Widget", "onReceivedError: " + error + " request: " + request);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(GONE);
                    webView.setVisibility(GONE);
                    statusText.setVisibility(View.VISIBLE);
                }
            });

            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.e("ChatWindow Widget", "onReceivedError: " + errorCode + " d: " + description + " url: " + failingUrl);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            final Uri uri = Uri.parse(url);
            return handleUri(view, uri);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            return handleUri(view, uri);
        }

        private boolean handleUri(WebView webView, final Uri uri) {
            String uriString = uri.toString();
            boolean facebookLogin = uriString.matches("https://.+facebook.+(/dialog/oauth\\?|/login\\.php\\?|/dialog/return/arbiter\\?).+");

            if (facebookLogin) {
                return false;
            } else {
                if (webViewPopup != null) {
                    webViewPopup.setVisibility(GONE);
                    removeView(webViewPopup);
                    webViewPopup = null;
                }

                String originalUrl = webView.getOriginalUrl();

                if (uriString.equals(originalUrl)) {
                    return false;
                } else {
                    if (chatWindowListener != null && chatWindowListener.handleUri(uri)) {

                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        getContext().startActivity(intent);
                    }

                    return true;
                }
            }
        }
    }

    class LCWebChromeClient extends WebChromeClient {
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            webViewPopup = new WebView(getContext());

            CookieManager cookieManager = CookieManager.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.getInstance().setAcceptThirdPartyCookies(webViewPopup, true);
            }

            webViewPopup.setVerticalScrollBarEnabled(false);
            webViewPopup.setHorizontalScrollBarEnabled(false);
            webViewPopup.setWebViewClient(new LCWebViewClient());
            webViewPopup.getSettings().setJavaScriptEnabled(true);
            webViewPopup.getSettings().setSavePassword(false);
            webViewPopup.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            addView(webViewPopup);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(webViewPopup);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            Log.d("onCloseWindow", "called");
        }

        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            chooseUriToUpload(uploadMsg);
        }

        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            chooseUriToUpload(uploadMsg);
        }

        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            chooseUriToUpload(uploadMsg);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {
            chooseUriArrayToUpload(uploadMsg);
            return true;
        }
    }

    private void receiveUploadedUriArray(Intent data) {
        Uri[] uploadedUris;
        try {
            uploadedUris = new Uri[]{Uri.parse(data.getDataString())};
        } catch (Exception e) {
            uploadedUris = null;
        }

        mUriArrayUploadCallback.onReceiveValue(uploadedUris);
        mUriArrayUploadCallback = null;
    }

    private void receiveUploadedUriPreHoneycomb(Intent data) {
        Uri uploadedUri = data.getData();

        mUriUploadCallback.onReceiveValue(uploadedUri);
        mUriUploadCallback = null;
    }

    private void receiveUploadedUri(Intent data) {
        Uri uploadedFileUri;
        try {
            String uploadedUriFilePath = UriUtils.getFilePathFromUri(getContext(), data.getData());
            File uploadedFile = new File(uploadedUriFilePath);
            uploadedFileUri = Uri.fromFile(uploadedFile);
        } catch (Exception e) {
            uploadedFileUri = null;
        }

        mUriUploadCallback.onReceiveValue(uploadedFileUri);
        mUriUploadCallback = null;
    }

    private void resetAllUploadCallbacks() {
        resetUriUploadCallback();
        resetUriArrayUploadCallback();
    }

    private void resetUriUploadCallback() {
        if (mUriUploadCallback != null) {
            mUriUploadCallback.onReceiveValue(null);
            mUriUploadCallback = null;
        }
    }

    private void resetUriArrayUploadCallback() {
        if (mUriArrayUploadCallback != null) {
            mUriArrayUploadCallback.onReceiveValue(null);
            mUriArrayUploadCallback = null;
        }
    }

    private void chooseUriToUpload(ValueCallback<Uri> uriValueCallback) {
        resetAllUploadCallbacks();
        mUriUploadCallback = uriValueCallback;
        startFileChooserActivity();
    }

    private void chooseUriArrayToUpload(ValueCallback<Uri[]> uriArrayValueCallback) {
        resetAllUploadCallbacks();
        mUriArrayUploadCallback = uriArrayValueCallback;
        startFileChooserActivity();
    }

    private void startFileChooserActivity() {
        if (chatWindowListener != null) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            chatWindowListener.onStartFilePickerActivity(intent, REQUEST_CODE_FILE_UPLOAD);
        } else {
            Log.e("ChatWindowView", "You must provide a listener to handle file sharing");
            Toast.makeText(getContext(), R.string.cant_share_files, Toast.LENGTH_SHORT).show();
        }
    }


    public interface ChatWindowEventsListener {
        void onChatWindowVisibilityChanged(boolean visible);

        void onNewMessage(NewMessageModel message, boolean windowVisible);

        void onStartFilePickerActivity(Intent intent, int requestCode);

        /*
            Return true to disable default uri handling.
         */
        boolean handleUri(final Uri uri);
    }
}
