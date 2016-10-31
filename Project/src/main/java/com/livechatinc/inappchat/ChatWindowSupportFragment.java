package com.livechatinc.inappchat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

public final class ChatWindowSupportFragment extends Fragment {
    private static final String KEY_LICENCE_NUMBER = "KEY_LICENCE_NUMBER_FRAGMENT";
    private static final String KEY_GROUP_ID = "KEY_GROUP_ID_FRAGMENT";
    private static final String KEY_VISITOR_NAME = "KEY_VISITOR_NAME_FRAGMENT";
    private static final String KEY_VISITOR_EMAIL = "KEY_VISITOR_EMAIL_FRAGMENT";

    private static final String DEFAULT_LICENCE_NUMBER = "-1";
    private static final String DEFAULT_GROUP_ID = "-1";

    private static final int REQUEST_CODE_FILE_UPLOAD = 21354;

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private TextView mTextView;

    private ValueCallback<Uri> mUriUploadCallback;
    private ValueCallback<Uri[]> mUriArrayUploadCallback;

    private String mLicenceNumber = DEFAULT_LICENCE_NUMBER;
    private String mGroupId = DEFAULT_GROUP_ID;
    private String mVisitorName;
    private String mVisitorEmail;

    public static ChatWindowSupportFragment newInstance(Object licenceNumber, Object groupId) {
        Bundle arguments = new Bundle();
        arguments.putString(KEY_LICENCE_NUMBER, String.valueOf(licenceNumber));
        arguments.putString(KEY_GROUP_ID, String.valueOf(groupId));

        ChatWindowSupportFragment chatWindowSupportFragment = new ChatWindowSupportFragment();
        chatWindowSupportFragment.setArguments(arguments);

        return chatWindowSupportFragment;
    }

    public static ChatWindowSupportFragment newInstance(Object licenceNumber, Object groupId, String visitorName, String visitorEmail) {
        Bundle arguments = new Bundle();
        arguments.putString(KEY_LICENCE_NUMBER, String.valueOf(licenceNumber));
        arguments.putString(KEY_GROUP_ID, String.valueOf(groupId));
        arguments.putString(KEY_VISITOR_NAME, visitorName);
        arguments.putString(KEY_VISITOR_EMAIL, visitorEmail);

        ChatWindowSupportFragment chatWindowSupportFragment = new ChatWindowSupportFragment();
        chatWindowSupportFragment.setArguments(arguments);

        return chatWindowSupportFragment;
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
        FrameLayout frameLayout = new FrameLayout(getActivity());

        mWebView = new WebView(getActivity());

        if (Build.VERSION.RELEASE.matches("4\\.4(\\.[12])?")) {
            String userAgentString = mWebView.getSettings().getUserAgentString();
            mWebView.getSettings().setUserAgentString(userAgentString + " AndroidNoFilesharing");
        }

        mWebView.setFocusable(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.requestFocus(View.FOCUS_DOWN);
        mWebView.setVisibility(View.GONE);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
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
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
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
        });

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

        frameLayout.addView(mWebView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(mProgressBar, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        frameLayout.addView(mTextView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return frameLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
}
