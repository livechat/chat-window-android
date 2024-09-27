package com.livechatinc.inappchat;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.livechatinc.inappchat.models.NewMessageModel;

import java.util.HashMap;

public final class ChatWindowFragment extends Fragment implements ChatWindowEventsListener {

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

    private ChatWindowConfiguration configuration;
    private ChatWindowView chatWindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            configuration = ChatWindowConfiguration.fromBundle(getArguments());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        chatWindow = (ChatWindowView) inflater.inflate(
                R.layout.view_chat_window,
                container,
                false
        );

        chatWindow.setEventsListener(this);
        chatWindow.supportAttachments(
                requireActivity().getActivityResultRegistry(),
                getLifecycle(),
                this
        );
        chatWindow.init(configuration);
        chatWindow.showChatWindow();

        return (View) chatWindow;
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
    public void onFilePickerActivityNotFound() {
    }

    @Override
    public void onWindowInitialized() {
    }

    @Override
    public void onRequestAudioPermissions(String[] permissions, int requestCode) {
    }

    @Override
    public boolean onError(ChatWindowErrorType errorType, int errorCode, String errorDescription) {
        return false;
    }
}
