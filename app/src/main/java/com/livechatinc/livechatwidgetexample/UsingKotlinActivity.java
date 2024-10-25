package com.livechatinc.livechatwidgetexample;

import static java.sql.DriverManager.println;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.livechatinc.chatwidget.ChatWidget;
import com.livechatinc.chatwidget.src.ChatWidgetCallbackListener;
import com.livechatinc.chatwidget.src.models.ChatMessage;
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig;

import java.util.HashMap;
import java.util.Map;

public class UsingKotlinActivity extends Activity {
    public ChatWidget chatWidget;
    public View loadingIndicator;
    public Button showChatButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_window_kotlin_example);

        chatWidget = findViewById(R.id.chat_widget);
        loadingIndicator = findViewById(R.id.loader_indicator);
        showChatButton = findViewById(R.id.show_chat_button);

        showChatButton.setOnClickListener(v -> {
            chatWidget.setVisibility(View.VISIBLE);
            showChatButton.setVisibility(View.INVISIBLE);

        });
        Map<String, String> customVariables = new HashMap<>();
        customVariables.put("key", "value");

        chatWidget.init(new ChatWidgetConfig("1520", "0", "Szymon", "email@mail.com", customVariables));
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
                    public void onChatMessage(@Nullable ChatMessage message) {
                        println("### onChatMessage: $message");
                    }
                }
        );
    }
}
