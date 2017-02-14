package com.livechatinc.inappchat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Łukasz Jerciński on 09/02/2017.
 */

public final class ChatWindowFragment extends Fragment {
    private static final String KEY_LICENCE_NUMBER = "KEY_LICENCE_NUMBER_FRAGMENT";
    private static final String KEY_GROUP_ID = "KEY_GROUP_ID_FRAGMENT";
    private static final String KEY_VISITOR_NAME = "KEY_VISITOR_NAME_FRAGMENT";
    private static final String KEY_VISITOR_EMAIL = "KEY_VISITOR_EMAIL_FRAGMENT";

    private static final String DEFAULT_LICENCE_NUMBER = "-1";
    private static final String DEFAULT_GROUP_ID = "-1";

    private static final int REQUEST_CODE_FILE_UPLOAD = 21354;

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private WebView mWebviewPopup;
    private Context mContext;
    private FrameLayout mContainer;
    private TextView mTextView;

    private ValueCallback<Uri> mUriUploadCallback;
    private ValueCallback<Uri[]> mUriArrayUploadCallback;

    private String mLicenceNumber = DEFAULT_LICENCE_NUMBER;
    private String mGroupId = DEFAULT_GROUP_ID;
    private String mVisitorName;
    private String mVisitorEmail;

    public static ChatWindowFragment newInstance(Object licenceNumber, Object groupId) {
        Bundle arguments = new Bundle();
        arguments.putString(KEY_LICENCE_NUMBER, String.valueOf(licenceNumber));
        arguments.putString(KEY_GROUP_ID, String.valueOf(groupId));

        ChatWindowFragment chatWindowFragment = new ChatWindowFragment();
        chatWindowFragment.setArguments(arguments);

        return chatWindowFragment;
    }

    public static ChatWindowFragment newInstance(Object licenceNumber, Object groupId, String visitorName, String visitorEmail) {
        Bundle arguments = new Bundle();
        arguments.putString(KEY_LICENCE_NUMBER, String.valueOf(licenceNumber));
        arguments.putString(KEY_GROUP_ID, String.valueOf(groupId));
        arguments.putString(KEY_VISITOR_NAME, visitorName);
        arguments.putString(KEY_VISITOR_EMAIL, visitorEmail);

        ChatWindowFragment chatWindowFragment = new ChatWindowFragment();
        chatWindowFragment.setArguments(arguments);

        return chatWindowFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mLicenceNumber = getArguments().getString(KEY_LICENCE_NUMBER);
            mGroupId = getArguments().getString(KEY_GROUP_ID);
            mVisitorName = getArguments().getString(KEY_VISITOR_NAME);
            mVisitorEmail = getArguments().getString(KEY_VISITOR_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainer = new FrameLayout(getActivity());
        mWebView = new WebView(getActivity());

        if (Build.VERSION.RELEASE.matches("4\\.4(\\.[12])?")) {
            String userAgentString = mWebView.getSettings().getUserAgentString();
            mWebView.getSettings().setUserAgentString(userAgentString + " AndroidNoFilesharing");
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        mWebView.setFocusable(true);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        }

        mWebView.setWebViewClient(new LCWebViewClient());
        mWebView.setWebChromeClient(new LCWebChromeClient());

        mWebView.requestFocus(View.FOCUS_DOWN);
        mWebView.setVisibility(View.GONE);

        mWebView.setOnTouchListener(new View.OnTouchListener() {
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

        mProgressBar = new ProgressBar(getActivity());
        mProgressBar.setVisibility(View.GONE);

        mTextView = new TextView(getActivity());
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setText("Couldn't load chat.");
        mTextView.setVisibility(View.GONE);

        mContainer.addView(mWebView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContainer.addView(mProgressBar, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        mContainer.addView(mTextView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return mContainer;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity().getApplicationContext();

        new LoadWebViewContentTask(mWebView, mProgressBar, mTextView).execute(mLicenceNumber, mGroupId, mVisitorName, mVisitorEmail);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FILE_UPLOAD) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                receiveUploadedData(data);
            } else {
                resetAllUploadCallbacks();
            }
        }
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

    private void receiveUploadedUriArray(Intent data) {
        Uri[] uploadedUris;
        try {
            uploadedUris = new Uri[] { Uri.parse(data.getDataString()) };
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
            String uploadedUriFilePath = UriUtils.getFilePathFromUri(getActivity(), data.getData());
            File uploadedFile = new File(uploadedUriFilePath);
            uploadedFileUri = Uri.fromFile(uploadedFile);
        } catch (Exception e) {
            uploadedFileUri = null;
        }

        mUriUploadCallback.onReceiveValue(uploadedFileUri);
        mUriUploadCallback = null;
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
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "Choose file to upload"), REQUEST_CODE_FILE_UPLOAD);
        } catch (ActivityNotFoundException e) {
            // no-op
        }
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

    class LCWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.post(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                }
            });

            if (url.startsWith("https://www.facebook.com/dialog/return/arbiter")) {
                if (mWebviewPopup != null) {
                    mWebviewPopup.setVisibility(View.GONE);
                    mContainer.removeView(mWebviewPopup);
                    mWebviewPopup = null;
                }
            }

            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                    mWebView.setVisibility(View.GONE);
                    mTextView.setVisibility(View.VISIBLE);
                }
            });

            super.onReceivedError(view, request, error);
            Log.e("LiveChat Widget", "onReceivedError: " + error + " request: " + request);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                    mWebView.setVisibility(View.GONE);
                    mTextView.setVisibility(View.VISIBLE);
                }
            });

            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.e("LiveChat Widget", "onReceivedError: " + errorCode + " d: " + description + " url: " + failingUrl);
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
                if (mWebviewPopup != null) {
                    mWebviewPopup.setVisibility(View.GONE);
                    mContainer.removeView(mWebviewPopup);
                    mWebviewPopup = null;
                }

                String originalUrl = mWebView.getOriginalUrl();

                if (uriString.equals(originalUrl)) {
                    return false;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }
            }
        }
    }

    class LCWebChromeClient extends WebChromeClient {
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            mWebviewPopup = new WebView(mContext);

            CookieManager cookieManager = CookieManager.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.getInstance().setAcceptThirdPartyCookies(mWebviewPopup, true);
            }

            mWebviewPopup.setVerticalScrollBarEnabled(false);
            mWebviewPopup.setHorizontalScrollBarEnabled(false);
            mWebviewPopup.setWebViewClient(new LCWebViewClient());
            mWebviewPopup.getSettings().setJavaScriptEnabled(true);
            mWebviewPopup.getSettings().setSavePassword(false);
            mWebviewPopup.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mWebviewPopup);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPopup);
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
}
