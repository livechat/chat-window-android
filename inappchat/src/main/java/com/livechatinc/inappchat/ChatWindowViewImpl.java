package com.livechatinc.inappchat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.volley.toolbox.Volley;

import java.io.File;

/**
 * Created by szymonjarosz on 19/07/2017.
 */

public class ChatWindowViewImpl extends FrameLayout implements ChatWindowView {
    private WebView webView;
    private TextView statusText;
    private Button reloadButton;
    private ProgressBar progressBar;
    protected WebView webViewPopup;
    private ChatWindowEventsListener eventsListener;
    private static final int REQUEST_CODE_FILE_UPLOAD = 21354;
    private static final int REQUEST_CODE_AUDIO_PERMISSIONS = 89292;

    private ValueCallback<Uri> mUriUploadCallback;
    private ValueCallback<Uri[]> mUriArrayUploadCallback;
    private ViewTreeObserver.OnGlobalLayoutListener layoutListener;
    private PermissionRequest webRequestPermissions;
    private ChatWindowViewModel viewModel;

    private final String TAG = "ChatWindowView";

    public ChatWindowViewImpl(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ChatWindowViewImpl(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setFitsSystemWindows(true);
        setVisibility(GONE);
        LayoutInflater.from(context).inflate(R.layout.view_chat_window_internal, this, true);
        webView = findViewById(R.id.chat_window_web_view);
        statusText = findViewById(R.id.chat_window_status_text);
        progressBar = findViewById(R.id.chat_window_progress);
        reloadButton = findViewById(R.id.chat_window_button);
        reloadButton.setOnClickListener(view -> reload(true));
        viewModel = new ChatWindowViewModel(this, Volley.newRequestQueue(context));

        if (Build.VERSION.RELEASE.matches("4\\.4(\\.[12])?")) {
            String userAgentString = webView.getSettings().getUserAgentString();
            webView.getSettings().setUserAgentString(userAgentString + " AndroidNoFilesharing");
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        webView.setFocusable(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        webView.setWebViewClient(new LCWebViewClient(this, viewModel));
        webView.setWebChromeClient(new LCWebChromeClient());

        webView.requestFocus(View.FOCUS_DOWN);
        webView.setVisibility(GONE);

        webView.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                    if (!view.hasFocus()) {
                        view.requestFocus();
                    }
                    break;
            }
            return false;
        });
        webView.addJavascriptInterface(new ChatWindowJsInterface(viewModel), ChatWindowJsInterface.BRIDGE_OBJECT_NAME);
        adjustResizeOnGlobalLayout(webView, getActivity());
    }

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void adjustResizeOnGlobalLayout(final WebView webView, final Activity activity) {
        if (!shouldAdjustLayout(getActivity())) return;
        final View decorView = activity.getWindow().getDecorView();
        layoutListener = () -> {
            final View decorView1 = getActivity().getWindow().getDecorView();
            final ViewGroup viewGroup = ChatWindowViewImpl.this;

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            Rect rect = new Rect();
            decorView1.getWindowVisibleDisplayFrame(rect);
            int paddingBottom = displayMetrics.heightPixels - rect.bottom;

            if (viewGroup.getPaddingBottom() != paddingBottom) {
                // showing/hiding the soft keyboard
                viewGroup.setPadding(viewGroup.getPaddingLeft(), viewGroup.getPaddingTop(), viewGroup.getPaddingRight(), paddingBottom);
            } else {
                // soft keyboard shown/hidden and padding changed
                if (paddingBottom != 0) {
                    // soft keyboard shown, scroll active element into view in case it is blocked by the soft keyboard
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        webView.evaluateJavascript("if (document.activeElement) { document.activeElement.scrollIntoView({behavior: \"smooth\", block: \"center\", inline: \"nearest\"}); }", null);
                    }
                }
            }
        };

        decorView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeLayoutListener();
        webView.destroy();
        super.onDetachedFromWindow();
    }

    private void removeLayoutListener() {
        if (layoutListener == null) return;
        final View decorView = getActivity().getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
        } else {
            decorView.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
        }
    }

    private boolean shouldAdjustLayout(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return false;
        }
        final int flags = activity.getWindow().getAttributes().flags;
        return (flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
    }

    @Override
    public boolean setConfiguration(@NonNull ChatWindowConfiguration config) {
        return viewModel.setConfig(config);
    }

    @Override
    public void initialize() {
        viewModel.init();
    }

    @Override
    public void reload(Boolean fullReload) {
        viewModel.reinitialize();
    }

    protected void showProgress() {
        progressBar.setVisibility(VISIBLE);

        webView.setVisibility(GONE);
        statusText.setVisibility(GONE);
        reloadButton.setVisibility(GONE);
    }

    protected void onHideChatWindow() {
        post(this::hideChatWindow);
    }

    @Override
    public void showChatWindow() {
        ChatWindowViewImpl.this.setVisibility(VISIBLE);
        if (eventsListener != null) {
            post(() -> eventsListener.onChatWindowVisibilityChanged(true));
        }
    }

    @Override
    public void hideChatWindow() {
        ChatWindowViewImpl.this.setVisibility(GONE);
        if (eventsListener != null) {
            post(() -> eventsListener.onChatWindowVisibilityChanged(false));
        }
    }

    @Override
    public boolean isChatLoaded() {
        return viewModel.chatUiReady;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_AUDIO_PERMISSIONS && webRequestPermissions != null) {
            String[] PERMISSIONS = {
                    PermissionRequest.RESOURCE_AUDIO_CAPTURE,
                    PermissionRequest.RESOURCE_VIDEO_CAPTURE
            };
            webRequestPermissions.grant(PERMISSIONS);
            webRequestPermissions = null;

            return true;

        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        if (ChatWindowViewImpl.this.isShown()) {
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

    @Override
    public void setEventsListener(ChatWindowEventsListener listener) {
        eventsListener = listener;
        viewModel.setEventsListener(listener);
    }

    private void receiveUploadedData(Intent data) {
        if (isUriArrayUpload()) {
            receiveUploadedUriArray(data);
        } else {
            receiveUploadedUri(data);
        }
    }

    private boolean isUriArrayUpload() {
        return mUriArrayUploadCallback != null;
    }

    protected void hideProgressBar() {
        progressBar.setVisibility(GONE);
    }

    public void loadUrl(String chatUrl) {
        webView.loadUrl(chatUrl);
    }


    private void onErrorDetected(boolean errorHandled, ChatWindowErrorType errorType, int errorCode, String errorDescription) {
        progressBar.setVisibility(GONE);
        if (!errorHandled) {
            if (viewModel.chatUiReady && errorType == ChatWindowErrorType.WebViewClient && errorCode == -2) {
                //Internet connection error. Connection issues handled in the chat window
                return;
            }
            showErrorView();
        }
    }

    protected void showErrorView() {
        webView.setVisibility(GONE);
        statusText.setVisibility(VISIBLE);
        reloadButton.setVisibility(VISIBLE);
    }

    class LCWebChromeClient extends WebChromeClient {
        @Override
        public boolean onCreateWindow(
                WebView view,
                boolean isDialog,
                boolean isUserGesture,
                Message resultMsg
        ) {
            webViewPopup = new WebView(getContext());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(webViewPopup, true);
            }

            webViewPopup.setVerticalScrollBarEnabled(false);
            webViewPopup.setHorizontalScrollBarEnabled(false);
            webViewPopup.setWebViewClient(new LCWebViewClient(ChatWindowViewImpl.this, viewModel));
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
            Log.d(TAG, "onCloseWindow");
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

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            webRequestPermissions = request;
            String[] runtimePermissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.MODIFY_AUDIO_SETTINGS};
            eventsListener.onRequestAudioPermissions(runtimePermissions, REQUEST_CODE_AUDIO_PERMISSIONS);
        }

        @Override
        public boolean onConsoleMessage(final ConsoleMessage consoleMessage) {
            if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                final boolean errorHandled = eventsListener != null && eventsListener.onError(ChatWindowErrorType.Console, -1, consoleMessage.message());
                post(() -> onErrorDetected(errorHandled, ChatWindowErrorType.Console, -1, consoleMessage.message()));
            }
            Log.i(TAG, "onConsoleMessage" + consoleMessage.messageLevel().name() + " " + consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
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
        if (eventsListener != null) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            eventsListener.onStartFilePickerActivity(intent, REQUEST_CODE_FILE_UPLOAD);
        } else {
            Log.e(TAG, "You must provide a listener to handle file sharing");
            Toast.makeText(getContext(), R.string.cant_share_files, Toast.LENGTH_SHORT).show();
        }
    }
}
