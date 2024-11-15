package com.livechatinc.livechatwidgetexample;

import static java.sql.DriverManager.println;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.livechatinc.chatwidget.ChatWidget;
import com.livechatinc.chatwidget.src.ChatWidgetCallbackListener;
import com.livechatinc.chatwidget.src.models.ChatMessage;
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig;

import java.util.HashMap;
import java.util.Map;

public class UsingKotlinActivity extends AppCompatActivity {
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
        chatWidget.init(new ChatWidgetConfig("1520", "0", "Szymon", "email@mail.com", customVariables));
    }
}
