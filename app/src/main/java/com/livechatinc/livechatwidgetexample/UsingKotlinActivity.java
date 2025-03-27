package com.livechatinc.livechatwidgetexample;

import static java.sql.DriverManager.println;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.livechatinc.chatwidget.LiveChatView;
import com.livechatinc.chatwidget.src.LiveChatViewCallbackListener;
import com.livechatinc.chatwidget.src.models.ChatMessage;
import com.livechatinc.chatwidget.src.models.CookieGrant;

import kotlin.Unit;

public class UsingKotlinActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    public LiveChatView liveChatView;
    public View loadingIndicator;
    public Button showChatButton;
    public Button reloadButton;
    private LiveChatViewCallbackListener liveChatViewCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_window_kotlin_example);

        liveChatView = findViewById(R.id.chat_widget);
        loadingIndicator = findViewById(R.id.loader_indicator);
        showChatButton = findViewById(R.id.show_chat_button);
        reloadButton = findViewById(R.id.reload_button);

        showChatButton.setOnClickListener(v -> {
            liveChatView.setVisibility(View.VISIBLE);
            showChatButton.setVisibility(View.INVISIBLE);
        });

        reloadButton.setOnClickListener(v -> {
            liveChatView.init(liveChatViewCallback);
            reloadButton.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.VISIBLE);
        });


        liveChatView.setIdentityCallback(this::saveCookieGrantToPreferences);

        liveChatViewCallback = liveChatCallback();

        final CookieGrant cookieGrant = readTokenCookiesFromPreferences();

        liveChatView.init(liveChatViewCallback);
    }

    @NonNull
    private LiveChatViewCallbackListener liveChatCallback() {
        return new LiveChatViewCallbackListener() {
            @Override
            public void onHide() {
                liveChatView.setVisibility(View.INVISIBLE);
                showChatButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded() {
                loadingIndicator.setVisibility(View.GONE);
                liveChatView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(@NonNull Throwable cause) {
                println("### onError: " + cause);
                liveChatView.setVisibility(View.GONE);
                reloadButton.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onNewMessage(@Nullable ChatMessage message) {
                println("### onChatMessage: $message");
            }

            @Override
            public void onFileChooserActivityNotFound() {
                Toast.makeText(UsingKotlinActivity.this, "File chooser activity not found", Toast.LENGTH_SHORT).show();
            }
        };
    }


    private Unit saveCookieGrantToPreferences(CookieGrant cookieGrant) {
        SharedPreferences sharedPreferences =
                getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cookieGrant", gson.toJson(cookieGrant));
        editor.apply();

        return Unit.INSTANCE;
    }

    private CookieGrant readTokenCookiesFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        final String cookieGrant = sharedPreferences.getString("cookieGrant", null);
        if (cookieGrant == null) {
            return null;
        }

        return gson.fromJson(cookieGrant, CookieGrant.class);
    }
}
