package com.livechatinc.livechatwidgetexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.livechatinc.inappchat.ChatWindowConfiguration;
import com.livechatinc.inappchat.ChatWindowErrorType;
import com.livechatinc.inappchat.ChatWindowEventsListener;
import com.livechatinc.inappchat.ChatWindowView;
import com.livechatinc.inappchat.models.NewMessageModel;

/**
 * Created by szymonjarosz on 26/07/2017.
 */

public class EmbeddedChatWindowFragmentExample extends Fragment implements ChatWindowEventsListener,
        MainActivity.OnBackPressedListener {
    private static final String START_CHAT_TEXT = "Show chat";
    private Button startChatBtn;
    private Button reloadChatBtn;
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
        startChatBtn = view.findViewById(R.id.embedded_start_chat);
        startChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatWindow.showChatWindow();
            }
        });
        reloadChatBtn = view.findViewById(R.id.embedded_reload_chat);
        reloadChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                chatWindow.reload(false);
            }
        });
        chatWindow = view.findViewById(R.id.embedded_chat_window);

        chatWindow.setConfiguration(ChatWindowConfiguration.fromBundle(getArguments()));
        chatWindow.setEventsListener(this);
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
    public void onWindowInitialized() {
    }

    @Override
    public void onStartFilePickerActivity(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onRequestAudioPermissions(String[] permissions, int requestCode) {
    }


    @Override
    public boolean onError(ChatWindowErrorType errorType, int errorCode, String errorDescription) {
        if (isAdded()) {
            if (errorType == ChatWindowErrorType.WebViewClient && errorCode == -2 && chatWindow.isChatLoaded()) {
                //Chat window can handle reconnection. You might want to delegate this to chat window
                return false;
            } else {
                reloadChatBtn.setVisibility(View.VISIBLE);
            }
            Toast.makeText(getActivity(), errorDescription, Toast.LENGTH_SHORT).show();
        }
        return true;
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
