package com.livechatinc.inappchat;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Łukasz Jerciński on 09/02/2017.
 */

class LoadWebViewContentTask extends AsyncTask<Map<String, String>, Void, String> {
    private static final String URL_STRING = "https://cdn.livechatinc.com/app/mobile/urls.json";
    private static final String JSON_CHAT_URL = "chat_url";

    private static final String PLACEHOLDER_LICENCE = "{%license%}";
    private static final String PLACEHOLDER_GROUP = "{%group%}";

    private final WebView mWebView;
    private final ProgressBar mProgressBar;
    private final TextView mTextView;
    private final Button mReloadButton;

    public LoadWebViewContentTask(WebView webView, ProgressBar progressBar, TextView textView, Button reloadButton) {
        mWebView = webView;
        mProgressBar = progressBar;
        mTextView = textView;
        mReloadButton = reloadButton;
    }

    @Override
    protected void onPreExecute() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(Map<String, String>... params) {
        HttpURLConnection urlConnection = null;
        try {
            URL urlObj = new URL(URL_STRING);
            urlConnection = (HttpURLConnection) urlObj.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                sb.toString();

                JSONObject jsonResponse = new JSONObject(sb.toString());

                String chatUrl = jsonResponse.getString(JSON_CHAT_URL);

                chatUrl = chatUrl.replace(PLACEHOLDER_LICENCE, params[0].get(ChatWindowFragment.KEY_LICENCE_NUMBER));
                chatUrl = chatUrl.replace(PLACEHOLDER_GROUP, params[0].get(ChatWindowFragment.KEY_GROUP_ID));
                chatUrl = chatUrl + "&native_platform=android";

                if (params[0].get(ChatWindowFragment.KEY_VISITOR_NAME) != null) {
                    chatUrl = chatUrl + "&name=" + URLEncoder.encode(params[0].get(ChatWindowFragment.KEY_VISITOR_NAME), "UTF-8").replace("+", "%20");
                }

                if (params[0].get(ChatWindowFragment.KEY_VISITOR_EMAIL) != null) {
                    chatUrl = chatUrl + "&email=" + URLEncoder.encode(params[0].get(ChatWindowFragment.KEY_VISITOR_EMAIL), "UTF-8");
                }

                final String customParams = escapeCustomParams(params[0], chatUrl);
                if (!TextUtils.isEmpty(customParams)) {
                    chatUrl = chatUrl + "&params=" + customParams;
                }

                if (!chatUrl.startsWith("http")) {
                    chatUrl = "https://" + chatUrl;
                }

                return chatUrl;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("LiveChat Widget", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            Log.e("LiveChat Widget", "Missing internet permission!");
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                try {
                    urlConnection.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null;
    }

    private String escapeCustomParams(Map<String, String> param, String chatUrl) {
        String params = "";
        for (String key : param.keySet()) {
            if (key.startsWith(ChatWindowFragment.CUSTOM_PARAM_PREFIX)) {
                final String encodedKey = Uri.encode(key.replace(ChatWindowFragment.CUSTOM_PARAM_PREFIX, ""));
                final String encodedValue = Uri.encode(param.get(key));

                if (!TextUtils.isEmpty(params)) {
                    params = params + "&";
                }

                params += encodedKey + "=" + encodedValue;
            }
        }
        return Uri.encode(params);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            mWebView.loadUrl(result);
            mWebView.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
            mReloadButton.setVisibility(View.VISIBLE);
        }
    }
}
