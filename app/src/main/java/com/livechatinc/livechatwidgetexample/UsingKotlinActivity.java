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
import com.livechatinc.chatwidget.ChatWidget;
import com.livechatinc.chatwidget.src.ChatWidgetCallbackListener;
import com.livechatinc.chatwidget.src.models.ChatMessage;
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig;
import com.livechatinc.chatwidget.src.models.CookieGrant;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;

public class UsingKotlinActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    public ChatWidget chatWidget;
    public View loadingIndicator;
    public Button showChatButton;
    public Button reloadButton;
    final ChatWidgetConfig config = new ChatWidgetConfig(
            BuildConfig.LICENCE,
            "0",
            "Szymon",
            "email@mail.com",
            BuildConfig.CLIENT_ID,
            BuildConfig.LICENCE_ID
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_window_kotlin_example);

        chatWidget = findViewById(R.id.chat_widget);
        loadingIndicator = findViewById(R.id.loader_indicator);
        showChatButton = findViewById(R.id.show_chat_button);
        reloadButton = findViewById(R.id.reload_button);

        showChatButton.setOnClickListener(v -> {
            chatWidget.setVisibility(View.VISIBLE);
            showChatButton.setVisibility(View.INVISIBLE);
        });

        reloadButton.setOnClickListener(v -> {
            chatWidget.init(config);
            reloadButton.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.VISIBLE);
        });

        Map<String, String> customVariables = new HashMap<>();
        customVariables.put("key", "value");

        chatWidget.setIdentityCallback(this::saveCookieGrantToPreferences);

        chatWidget.setCallbackListener(
                new ChatWidgetCallbackListener() {
                    @Override
                    public void hideChatWidget() {
                        chatWidget.setVisibility(View.INVISIBLE);
                        showChatButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void chatLoaded() {
                        loadingIndicator.setVisibility(View.GONE);
                        chatWidget.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(@NonNull Throwable cause) {
                        println("### onError: " + cause);
                        chatWidget.setVisibility(View.GONE);
                        reloadButton.setVisibility(View.VISIBLE);
                        loadingIndicator.setVisibility(View.GONE);
                    }

                    @Override
                    public void onChatMessage(@Nullable ChatMessage message) {
                        println("### onChatMessage: $message");
                    }

                    @Override
                    public void onFileChooserActivityNotFound() {
                        Toast.makeText(UsingKotlinActivity.this, "File chooser activity not found", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        final CookieGrant cookieGrant = readTokenCookiesFromPreferences();

        chatWidget.init(config.copyWith(null, null, null, null, null, null, null, cookieGrant));
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
