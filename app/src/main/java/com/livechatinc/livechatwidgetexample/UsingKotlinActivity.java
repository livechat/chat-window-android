package com.livechatinc.livechatwidgetexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.livechatinc.chatwidget.LiveChat;
import com.livechatinc.chatwidget.src.domain.interfaces.LiveChatViewInitListener;
import com.livechatinc.chatwidget.src.domain.models.IdentityGrant;
import com.livechatinc.chatwidget.src.presentation.LiveChatView;

import kotlin.Unit;

public class UsingKotlinActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    public LiveChatView liveChatView;
    public View loadingIndicator;
    public Button showChatButton;
    public Button reloadButton;
    private LiveChatViewInitListener liveChatViewCallback;

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


        liveChatViewCallback = liveChatCallback();

        final IdentityGrant identityRestorationGrant = readTokenCookiesFromPreferences();

        LiveChat.getInstance().configureIdentityProvider(
                BuildConfig.LICENSE_ID,
                BuildConfig.CLIENT_ID,
                identityGrant -> {
//                    saveCookieGrantToPreferences(cookieGrant);
                    Log.i("UsingKotlinActivity", "### new cookie grant: " + identityGrant);
                    return Unit.INSTANCE;
                }
        );

        LiveChat.getInstance().logInCustomer(identityRestorationGrant);

        liveChatView.attachTo(this);
        liveChatView.init(liveChatViewCallback);
    }

    @NonNull
    private LiveChatViewInitListener liveChatCallback() {
        return new LiveChatViewInitListener() {
            @Override
            public void onHide() {
                liveChatView.setVisibility(View.INVISIBLE);
                showChatButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUIReady() {
                loadingIndicator.setVisibility(View.GONE);
                liveChatView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(@NonNull Throwable cause) {
                liveChatView.setVisibility(View.GONE);
                reloadButton.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            }
        };
    }

    private Unit saveCookieGrantToPreferences(IdentityGrant identityGrant) {
        SharedPreferences sharedPreferences =
                getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cookieGrant", gson.toJson(identityGrant));
        editor.apply();

        return Unit.INSTANCE;
    }

    private IdentityGrant readTokenCookiesFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        final String cookieGrant = sharedPreferences.getString("cookieGrant", null);
        if (cookieGrant == null) {
            return null;
        }

        return gson.fromJson(cookieGrant, IdentityGrant.class);
    }
}
