package com.livechatinc.livechatwidgetexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.livechatinc.inappchat.ChatWindowView;
import com.livechatinc.inappchat.models.NewMessageModel;

/**
 * Created by szymonjarosz on 26/07/2017.
 */

public class EmbeddedChatWindowFragmentExample extends Fragment implements ChatWindowView.ChatWindowEventsListener,
        MainActivity.OnBackPressedListener {
    private static final String START_CHAT_TEXT = "Chat with support";
    private Button startChatBtn;
    private ChatWindowView chatWindow;
    private int counter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_embedded_example, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startChatBtn = (Button) view.findViewById(R.id.embedded_start_chat);
        startChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatWindow.showChatWindow();
            }
        });
        chatWindow = (ChatWindowView) view.findViewById(R.id.embedded_chat_window);
        chatWindow.setUpWindow(BaseApplication.getChatWindowConfiguration());
        chatWindow.setUpListener(this);
        chatWindow.initialize();
    }


    @Override
    public void onChatWindowVisibilityChanged(boolean visible) {
        if (visible) {
            counter = 0;
            startChatBtn.setText(START_CHAT_TEXT);
        }
    }

    @Override
    public void onNewMessage(NewMessageModel message, boolean windowVisible) {
        if (!windowVisible) {
            counter++;
            startChatBtn.setText(START_CHAT_TEXT + " (" + counter + ")");
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        chatWindow.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onBackPressed() {
        return chatWindow.onBackPressed();
    }

    public static Fragment newInstance() {
        return new EmbeddedChatWindowFragmentExample();
    }
}
