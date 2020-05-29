package com.livechatinc.livechatwidgetexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.livechatinc.inappchat.ChatWindowErrorType;
import com.livechatinc.inappchat.ChatWindowView;
import com.livechatinc.inappchat.models.NewMessageModel;

import static android.view.View.GONE;

/**
 * Created by szymonjarosz on 26/07/2017.
 */

public class FullScreenWindowActivityExample extends AppCompatActivity implements ChatWindowView.ChatWindowEventsListener {
    private FloatingActionButton startChatBtn;
    private ChatWindowView chatWindow;
    private TextView chatBadgeTv;
    private int badgeCounter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_window_launcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startChatBtn = (FloatingActionButton) findViewById(R.id.start_chat);
        startChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chatWindow == null || !chatWindow.isInitialized()) {
                    chatWindow = null;
                    chatWindow = ChatWindowView.createAndAttachChatWindowInstance(FullScreenWindowActivityExample.this);
                    chatWindow.setUpWindow(BaseApplication.getChatWindowConfiguration());
                    chatWindow.setUpListener(FullScreenWindowActivityExample.this);
                    chatWindow.initialize();
                }
                showChatWindow();
            }
        });
        chatBadgeTv = (TextView) findViewById(R.id.chat_badge);
    }

    private void showChatWindow() {
        chatWindow.showChatWindow();
    }

    @Override
    public void onChatWindowVisibilityChanged(boolean visible) {
        if (visible) {
            discardBadge();
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
    public void onStartFilePickerActivity(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onError(ChatWindowErrorType errorType, int errorCode, String errorDescription) {
        Toast.makeText(FullScreenWindowActivityExample.this, errorDescription, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!chatWindow.onBackPressed())
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chatWindow.onActivityResult(requestCode, resultCode, data);
    }
}
