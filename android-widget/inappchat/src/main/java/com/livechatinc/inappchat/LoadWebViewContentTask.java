package com.livechatinc.inappchat;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
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

/**
 * Created by Łukasz Jerciński on 09/02/2017.
 */

class LoadWebViewContentTask extends AsyncTask<String, Void, String> {
    private static final String URL_STRING = "https://cdn.livechatinc.com/app/mobile/urls.json";
    private static final String JSON_CHAT_URL = "chat_url";

    private static final String PLACEHOLDER_LICENCE = "{%license%}";
    private static final String PLACEHOLDER_GROUP = "{%group%}";

    private static final int INDEX_LICENCE_NUMBER = 0;
    private static final int INDEX_GROUP_ID = 1;
    private static final int INDEX_VISITOR_NAME = 2;
    private static final int INDEX_VISITOR_EMAIL = 3;

    private final WebView mWebView;
    private final ProgressBar mProgressBar;
    private final TextView mTextView;

    public LoadWebViewContentTask(WebView webView, ProgressBar progressBar, TextView textView) {
        mWebView = webView;
        mProgressBar = progressBar;
        mTextView = textView;
    }

    @Override
    protected void onPreExecute() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
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

                chatUrl = chatUrl.replace(PLACEHOLDER_LICENCE, params[INDEX_LICENCE_NUMBER]);
                chatUrl = chatUrl.replace(PLACEHOLDER_GROUP, params[INDEX_GROUP_ID]);

                if (params[INDEX_VISITOR_NAME] != null) {
                    chatUrl = chatUrl + "&name=" + URLEncoder.encode(params[INDEX_VISITOR_NAME], "UTF-8").replace("+", "%20");
                }

                if (params[INDEX_VISITOR_EMAIL] != null) {
                    chatUrl = chatUrl + "&email=" + URLEncoder.encode(params[INDEX_VISITOR_EMAIL], "UTF-8");
                }

                if (!chatUrl.startsWith("http")) {
                    chatUrl = "https://" + chatUrl;
                }

                return chatUrl;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            mWebView.loadUrl(result);
            mWebView.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        }
    }
}
