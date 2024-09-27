package com.livechatinc.inappchat;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatWindowConfiguration implements Serializable {
    public static final String KEY_LICENCE_NUMBER = "KEY_LICENCE_NUMBER";
    public static final String KEY_GROUP_ID = "KEY_GROUP_ID";
    public static final String KEY_VISITOR_NAME = "KEY_VISITOR_NAME";
    public static final String KEY_VISITOR_EMAIL = "KEY_VISITOR_EMAIL";

    private static final String DEFAULT_GROUP_ID = "0";
    private static final String TAG = ChatWindowConfiguration.class.getSimpleName();

    public static final String CUSTOM_PARAM_PREFIX = "#LCcustomParam_";

    public final String licenceNumber;
    public final String groupId;
    public final String visitorName;
    public final String visitorEmail;
    public final HashMap<String, String> customVariables;

    public ChatWindowConfiguration(
            @NonNull String licenceNumber,
            @Nullable String groupId,
            @Nullable String visitorName,
            @Nullable String visitorEmail,
            @Nullable HashMap<String, String> customVariables
    ) {
        this.licenceNumber = licenceNumber;
        this.groupId = groupId != null ? groupId : DEFAULT_GROUP_ID;
        this.visitorName = visitorName;
        this.visitorEmail = visitorEmail;
        this.customVariables = customVariables;
    }

    public static ChatWindowConfiguration fromBundle(Bundle arguments) {
        HashMap<String, String> customParams = new HashMap<>();
        for (String key : arguments.keySet()) {
            if (key.startsWith(CUSTOM_PARAM_PREFIX)) {
                customParams.put(key.replaceFirst(CUSTOM_PARAM_PREFIX, ""), arguments.getString(key));
            }
        }

        return new ChatWindowConfiguration.Builder()
                .setLicenceNumber(arguments.getString(KEY_LICENCE_NUMBER))
                .setGroupId(arguments.getString(KEY_GROUP_ID))
                .setVisitorName(arguments.getString(KEY_VISITOR_NAME))
                .setVisitorEmail(arguments.getString(KEY_VISITOR_EMAIL))
                .setCustomParams(customParams).build();
    }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(KEY_LICENCE_NUMBER, licenceNumber);
        params.put(KEY_GROUP_ID, groupId);
        if (!TextUtils.isEmpty(visitorName))
            params.put(KEY_VISITOR_NAME, visitorName);
        if (!TextUtils.isEmpty(visitorEmail))
            params.put(KEY_VISITOR_EMAIL, visitorEmail);
        if (customVariables != null) {
            for (String key : customVariables.keySet()) {
                params.put(CUSTOM_PARAM_PREFIX + key, customVariables.get(key));
            }
        }

        return params;
    }

    public Bundle asBundle() {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : getParams().entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }

        return bundle;
    }

    public String addParamsToChatWindowUrl(String chatUrl) {
        try {
            chatUrl = replaceParameter(chatUrl, "license", licenceNumber);
            chatUrl = replaceParameter(chatUrl, "group", groupId);

            chatUrl = chatUrl + "&native_platform=android";

            if (visitorName != null) {
                chatUrl = chatUrl + "&name=" + URLEncoder.encode(visitorName, "UTF-8").replace("+", "%20");
            }

            if (visitorEmail != null) {
                chatUrl = chatUrl + "&email=" + URLEncoder.encode(visitorEmail, "UTF-8");
            }

            final String customParams = encodeParams(getParams());

            if (!TextUtils.isEmpty(customParams)) {
                chatUrl = chatUrl + "&params=" + customParams;
            }

            if (!chatUrl.startsWith("http")) {
                chatUrl = "https://" + chatUrl;
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error while encoding URL: " + e.getMessage(), e);
        }

        return chatUrl;
    }

    private String replaceParameter(String url, String key, String value) {
        return url.replace("{%" + key + "%}", value);
    }

    private String encodeParams(Map<String, String> param) {
        String params = "";

        for (String key : param.keySet()) {
            if (key.startsWith(ChatWindowConfiguration.CUSTOM_PARAM_PREFIX)) {
                final String encodedKey = Uri.encode(key.replace(ChatWindowConfiguration.CUSTOM_PARAM_PREFIX, ""));
                final String encodedValue = Uri.encode(param.get(key));

                if (!TextUtils.isEmpty(params)) {
                    params = params + "&";
                }

                params += encodedKey + "=" + encodedValue;
            }
        }

        return Uri.encode(params);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatWindowConfiguration that = (ChatWindowConfiguration) o;

        if (!licenceNumber.equals(that.licenceNumber)) return false;
        if (!groupId.equals(that.groupId)) return false;
        if (!Objects.equals(visitorName, that.visitorName))
            return false;
        if (!Objects.equals(visitorEmail, that.visitorEmail))
            return false;
        return Objects.equals(customVariables, that.customVariables);
    }

    @Override
    public int hashCode() {
        int result = licenceNumber.hashCode();
        result = 31 * result + groupId.hashCode();
        result = 31 * result + (visitorName != null ? visitorName.hashCode() : 0);
        result = 31 * result + (visitorEmail != null ? visitorEmail.hashCode() : 0);
        result = 31 * result + (customVariables != null ? customVariables.hashCode() : 0);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "licenceNumber='" + licenceNumber + "'\n" +
                        "groupId='" + groupId + "'\n" +
                        "visitorName='" + visitorName + "'\n" +
                        "visitorEmail='" + visitorEmail + "'\n" +
                        "customVariables=" + customVariables;
    }

    public static class Builder {
        private String licenceNumber;
        private String groupId;
        private String visitorName;
        private String visitorEmail;
        private HashMap<String, String> customParams;

        public ChatWindowConfiguration build() {
            if (TextUtils.isEmpty(licenceNumber))
                throw new IllegalStateException("Licence Number cannot be null");
            return new ChatWindowConfiguration(licenceNumber, groupId, visitorName, visitorEmail, customParams);
        }

        public Builder setLicenceNumber(String licenceNr) {
            this.licenceNumber = licenceNr;
            return this;
        }

        public Builder setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder setVisitorName(String visitorName) {
            this.visitorName = visitorName;
            return this;
        }

        public Builder setVisitorEmail(String visitorEmail) {
            this.visitorEmail = visitorEmail;
            return this;
        }

        public Builder setCustomParams(HashMap<String, String> customParams) {
            this.customParams = customParams;
            return this;
        }
    }

}
