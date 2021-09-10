package com.livechatinc.livechatwidgetexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import static com.livechatinc.livechatwidgetexample.MainActivity.LIVECHAT_SUPPORT_LICENCE_NR;

/**
 * Created by szymonjarosz on 26/07/2017.
 */

public class FullScreenWindowActivityExample extends AppCompatActivity implements ChatWindowEventsListener {
    private FloatingActionButton startChatBtn;
    private ChatWindowView chatWindow;
    private TextView chatBadgeTv;
    private EditText editText;
    private Button clearSessionBtn;
    private Button setVisitorName;
    private int badgeCounter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_window_launcher);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chatWindow = ChatWindowUtils.createAndAttachChatWindowInstance(FullScreenWindowActivityExample.this);
        chatWindow.setConfiguration(BaseApplication.getChatWindowConfiguration());
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
        setVisitorName = findViewById(R.id.set_visitor_name);
        setVisitorName.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  boolean changed = chatWindow.setConfiguration(getNewConfig());
                                                  Log.i("TAG", "config changed: " + changed);
                                                  if (changed) {
                                                      chatWindow.reload(true);
                                                  }
                                              }
                                          }

        );
        clearSessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatWindowUtils.clearSession(view.getContext());
                chatWindow.reload(false);
            }
        });
        editText = findViewById(R.id.visitor_name);
    }

    public ChatWindowConfiguration getNewConfig() {
        return new ChatWindowConfiguration(LIVECHAT_SUPPORT_LICENCE_NR, "0", editText.getText().toString(), null, null);
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
        super.onActivityResult(requestCode, resultCode, data);
        chatWindow.onActivityResult(requestCode, resultCode, data);
    }
}
