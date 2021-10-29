package com.livechatinc.livechatwidgetexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.livechatinc.inappchat.ChatWindowConfiguration;
import com.livechatinc.inappchat.ChatWindowErrorType;
import com.livechatinc.inappchat.ChatWindowEventsListener;
import com.livechatinc.inappchat.ChatWindowUtils;
import com.livechatinc.inappchat.ChatWindowView;
import com.livechatinc.inappchat.models.NewMessageModel;

import static android.view.View.GONE;

/**
 * Created by szymonjarosz on 26/07/2017.
 */

public class FullScreenWindowActivityExample extends AppCompatActivity implements ChatWindowEventsListener {
    private FloatingActionButton startChatBtn;
    private ChatWindowView chatWindow;
    private TextView chatBadgeTv;
    private Button clearSessionBtn;
    private int badgeCounter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_window_launcher);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chatWindow = ChatWindowUtils.createAndAttachChatWindowInstance(FullScreenWindowActivityExample.this);
        chatWindow.setConfiguration((ChatWindowConfiguration) getIntent().getSerializableExtra("config"));
        chatWindow.setEventsListener(this);
        chatWindow.initialize();
        startChatBtn = findViewById(R.id.start_chat);
        startChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChatWindow();
            }
        });
        chatBadgeTv = findViewById(R.id.chat_badge);
        clearSessionBtn = findViewById(R.id.clear_session_btn);
        clearSessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatWindowUtils.clearSession(view.getContext());
                chatWindow.reload(false);
            }
        });
    }

    private void showChatWindow() {
        chatWindow.showChatWindow();
    }

    @Override
    public void onChatWindowVisibilityChanged(boolean visible) {
        if (visible) {
            discardBadge();
        } else {
            clearSessionBtn.setVisibility(View.VISIBLE);
        }
    }

    private void discardBadge() {
        badgeCounter = 0;
        chatBadgeTv.setVisibility(GONE);
        chatBadgeTv.setText("");
    }

    @Override
    public void onNewMessage(NewMessageModel message, boolean windowVisible) {
        if (!windowVisible) {
            badgeCounter++;
            chatBadgeTv.setVisibility(View.VISIBLE);
            chatBadgeTv.setText(String.valueOf(badgeCounter));
        }
    }

    @Override
    public boolean handleUri(Uri uri) {
        return false;
    }

    @Override
    public void onWindowInitialized() {

    }

    @Override
    public void onStartFilePickerActivity(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onRequestAudioPermissions(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(permissions, requestCode);
        }
    }

    @Override
    public boolean onError(ChatWindowErrorType errorType, int errorCode, String errorDescription) {
        Toast.makeText(FullScreenWindowActivityExample.this, errorDescription, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!chatWindow.onBackPressed())
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!chatWindow.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!chatWindow.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
