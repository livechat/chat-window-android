package com.livechatinc.inappchat;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.livechatinc.inappchat.models.NewMessageModel;

import java.util.HashMap;

/**
 * Created by Łukasz Jerciński on 09/02/2017.
 */

public final class ChatWindowFragment extends Fragment implements ChatWindowEventsListener {
    private ChatWindowConfiguration configuration;
    private ChatWindowView chatWindow;

    public static ChatWindowFragment newInstance(Object licenceNumber, Object groupId) {
        return newInstance(licenceNumber, groupId, null, null, null);
    }

    public static ChatWindowFragment newInstance(Object licenceNumber, Object groupId, @Nullable String visitorName, @Nullable String visitorEmail) {
        return newInstance(licenceNumber, groupId, visitorName, visitorEmail, null);
    }

    public static ChatWindowFragment newInstance(Object licenceNumber, Object groupId, @Nullable String visitorName, @Nullable String visitorEmail, @Nullable HashMap<String, String> customVariables) {
        Bundle arguments = new Bundle();
        arguments.putString(ChatWindowConfiguration.KEY_LICENCE_NUMBER, String.valueOf(licenceNumber));
        arguments.putString(ChatWindowConfiguration.KEY_GROUP_ID, String.valueOf(groupId));
        if (visitorName != null)
            arguments.putString(ChatWindowConfiguration.KEY_VISITOR_NAME, visitorName);
        if (visitorEmail != null)
            arguments.putString(ChatWindowConfiguration.KEY_VISITOR_EMAIL, visitorEmail);
        if (customVariables != null) {
            for (String key : customVariables.keySet()) {
                arguments.putString(ChatWindowConfiguration.CUSTOM_PARAM_PREFIX + key, customVariables.get(key));
            }
        }

        ChatWindowFragment chatWindowFragment = new ChatWindowFragment();
        chatWindowFragment.setArguments(arguments);

        return chatWindowFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ChatWindowConfiguration.Builder builder = new ChatWindowConfiguration.Builder();
        HashMap<String, String> customParams = new HashMap<>();

        if (getArguments() != null) {

            for (String key : getArguments().keySet()) {
                if (ChatWindowConfiguration.KEY_LICENCE_NUMBER.equals(key)) {
                    builder.setLicenceNumber(getArguments().getString(ChatWindowConfiguration.KEY_LICENCE_NUMBER));
                } else if (ChatWindowConfiguration.KEY_GROUP_ID.equals(key)) {
                    builder.setGroupId(getArguments().getString(ChatWindowConfiguration.KEY_GROUP_ID));
                } else if (ChatWindowConfiguration.KEY_VISITOR_NAME.equals(key)) {
                    builder.setVisitorName(getArguments().getString(ChatWindowConfiguration.KEY_VISITOR_NAME));
                } else if (ChatWindowConfiguration.KEY_VISITOR_EMAIL.equals(key)) {
                    builder.setVisitorEmail(getArguments().getString(ChatWindowConfiguration.KEY_VISITOR_EMAIL));
                } else {
                    customParams.put(key, String.valueOf(getArguments().get(key)));
                }
            }
            builder.setCustomParams(customParams);
        }
        configuration = builder.build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        chatWindow = (ChatWindowView) inflater.inflate(R.layout.view_chat_window, container, false);

        chatWindow.setConfiguration(configuration);
        chatWindow.setEventsListener(this);
        chatWindow.initialize();
        chatWindow.showChatWindow();
        return (View) chatWindow;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        chatWindow.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onChatWindowVisibilityChanged(boolean visible) {
        if (!visible) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onNewMessage(NewMessageModel message, boolean windowVisible) {

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
        return false;
    }
}
