package com.livechatinc.inappchat;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

/*package*/ class LoadWebViewContentTask extends AsyncTask<String, Void, String> {
    private static final String URL = "http://cdn.livechatinc.com/app/mobile/urls.json";
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
        try {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
            HttpConnectionParams.setSoTimeout(httpParams, 15000);

            HttpClient httpClient = new DefaultHttpClient(httpParams);

            HttpGet httpGet = new HttpGet(URL);
            HttpResponse httpResponse = httpClient.execute(httpGet);

            String responseString = EntityUtils.toString(httpResponse.getEntity());

            if (BuildConfig.DEBUG) {
                Log.d(getClass().getName(), responseString);
            }

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(responseString);

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
            } else {
                return null;
            }
        } catch (IOException | JSONException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
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
