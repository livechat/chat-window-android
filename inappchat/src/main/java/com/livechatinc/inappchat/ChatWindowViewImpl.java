package com.livechatinc.inappchat;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.android.volley.toolbox.Volley;

import java.io.File;

public class ChatWindowViewImpl extends FrameLayout implements ChatWindowView, ChatWindowViewInternal {
    private WebView webView;
    private TextView statusText;
    private Button reloadButton;
    private ProgressBar progressBar;
    protected static final int REQUEST_CODE_FILE_UPLOAD = 21354;
    protected static final int REQUEST_CODE_AUDIO_PERMISSIONS = 89292;

    private ValueCallback<Uri> mUriUploadCallback;
    private ValueCallback<Uri[]> mUriArrayUploadCallback;
    private ViewTreeObserver.OnGlobalLayoutListener layoutListener;
    protected PermissionRequest webRequestPermissions;
    private ChatWindowPresenter presenter;

    private final static String TAG = "ChatWindowView";
    private ChatWindowLifecycleObserver observer;
    private Observer<Uri> uriObserver;

    public ChatWindowViewImpl(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ChatWindowViewImpl(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        Log.d(TAG, "Initializing ChatWindowViewImpl");
        setFitsSystemWindows(true);
        setVisibility(GONE);
        LayoutInflater.from(context).inflate(R.layout.view_chat_window_internal, this, true);
        webView = findViewById(R.id.chat_window_web_view);
        statusText = findViewById(R.id.chat_window_status_text);
        progressBar = findViewById(R.id.chat_window_progress);
        reloadButton = findViewById(R.id.chat_window_button);
        reloadButton.setOnClickListener(view -> reload(true));
        presenter = new ChatWindowPresenter(this, Volley.newRequestQueue(context));

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
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        webView.setWebViewClient(new LCWebViewClient(presenter));
        webView.setWebChromeClient(new LCWebChromeClient(this, presenter));

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
        webView.addJavascriptInterface(new ChatWindowJsInterface(presenter), ChatWindowJsInterface.BRIDGE_OBJECT_NAME);
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

        if (observer != null && uriObserver != null) {
            observer.getResultLiveData().removeObserver(uriObserver);
        }

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

    // ChatWindowView interface

    @Override
    public boolean setConfiguration(@NonNull ChatWindowConfiguration config) {
        return presenter.setConfig(config);
    }

    @Override
    public void setEventsListener(ChatWindowEventsListener listener) {
        presenter.setEventsListener(listener);
    }

    @Override
    public void setUpAttachmentSupport(
            ActivityResultRegistry activityResultRegistry,
            Lifecycle lifecycle,
            LifecycleOwner owner
    ) {
        //TODO: consider supporting multiple files result as well
        observer = new ChatWindowLifecycleObserver(activityResultRegistry);
        lifecycle.addObserver(observer);

        uriObserver = this::onFileChooserResult;
        observer.getResultLiveData().observe(owner, uriObserver);
    }

    private void onFileChooserResult(Uri uri) {
        mUriArrayUploadCallback.onReceiveValue(new Uri[]{uri});
        mUriArrayUploadCallback = null;
    }

    @Override
    public void initialize() {
        presenter.init();
    }

    @Override
    public void reload(Boolean fullReload) {
        presenter.reinitialize();
    }

    @Override
    public void showChatWindow() {
        ChatWindowViewImpl.this.setVisibility(VISIBLE);
        if (presenter.eventsListener != null) {
            post(() -> presenter.eventsListener.onChatWindowVisibilityChanged(true));
        }
    }

    @Override
    public void hideChatWindow() {
        ChatWindowViewImpl.this.setVisibility(GONE);
        if (presenter.eventsListener != null) {
            post(() -> presenter.eventsListener.onChatWindowVisibilityChanged(false));
        }
    }

    @Override
    public boolean isChatLoaded() {
        return presenter.chatUiReady;
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
            hideChatWindow();

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

    // End of ChatWindowView interface

    // ChatWindowViewInternal interface

    @Override
    public void loadUrl(String chatUrl) {
        if (getContext() != null) {
            webView.loadUrl(chatUrl);
        }
    }

    @Override
    public void showWebView() {
        if (getContext() != null) {
            webView.setVisibility(VISIBLE);
        }
    }

    @Override
    public void showProgress() {
        if (getContext() != null) {
            progressBar.setVisibility(VISIBLE);

            webView.setVisibility(GONE);
            statusText.setVisibility(GONE);
            reloadButton.setVisibility(GONE);
        }
    }

    @Override
    public void hideProgressBar() {
        if (getContext() != null) {
            progressBar.setVisibility(GONE);
        }
    }

    @Override
    public void showErrorView() {
        if (getContext() != null) {
            webView.setVisibility(GONE);
            statusText.setVisibility(VISIBLE);
            reloadButton.setVisibility(VISIBLE);
        }
    }

    @Override
    public void launchExternalBrowser(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        webView.getContext().startActivity(intent);
    }

    @Override
    public void runOnMainThread(Runnable runnable) {
        post(runnable);
    }

    // End of ChatWindowViewInternal interface

    //TODO: possibly remove those methods
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

    protected void chooseUriToUpload(ValueCallback<Uri> uriValueCallback) {
        resetAllUploadCallbacks();
        mUriUploadCallback = uriValueCallback;
        startFileChooserActivity();
    }

    protected void chooseUriArrayToUpload(ValueCallback<Uri[]> uriArrayValueCallback) {
        resetAllUploadCallbacks();
        mUriArrayUploadCallback = uriArrayValueCallback;
        startFileChooserActivity();
    }

    private void startFileChooserActivity() {
        if (observer == null) {
            Toast.makeText(getContext(), "Attachment support is not set up", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Attachment support is not set up");
            return;
        }

        observer.selectFile();
    }
}
