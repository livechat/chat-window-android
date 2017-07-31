package com.livechatinc.inappchat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by szymonjarosz on 20/07/2017.
 */

public class ChatWindowConfiguration {
    public static final String KEY_LICENCE_NUMBER = "KEY_LICENCE_NUMBER_FRAGMENT";
    public static final String KEY_GROUP_ID = "KEY_GROUP_ID_FRAGMENT";
    public static final String KEY_VISITOR_NAME = "KEY_VISITOR_NAME_FRAGMENT";
    public static final String KEY_VISITOR_EMAIL = "KEY_VISITOR_EMAIL_FRAGMENT";

    private static final String DEFAULT_GROUP_ID = "-1";
    public static final String CUSTOM_PARAM_PREFIX = "#LCcustomParam_";

    private String licenceNumber;
    private String groupId;
    private String visitorName;
    private String visitorEmail;
    private HashMap<String, String> customVariables;

    public ChatWindowConfiguration(
            @NonNull String licenceNumber,
            @Nullable String groupId,
            @Nullable String visitorName,
            @Nullable String visitorEmail,
            @Nullable HashMap<String, String> customVariables) {
        this.licenceNumber = licenceNumber;
        this.groupId = groupId;
        this.visitorName = visitorName;
        this.visitorEmail = visitorEmail;
        this.customVariables = customVariables;
    }

    Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(KEY_LICENCE_NUMBER, String.valueOf(licenceNumber));
        params.put(KEY_GROUP_ID, groupId != null ? String.valueOf(groupId) : DEFAULT_GROUP_ID);
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

        Builder setLicenceNumber(String licenceNr) {
            this.licenceNumber = licenceNr;
            return this;
        }

        Builder setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        Builder setVisitorName(String visitorName) {
            this.visitorName = visitorName;
            return this;
        }

        Builder setVisitorEmail(String visitorEmail) {
            this.visitorEmail = visitorEmail;
            return this;
        }

        Builder setCustomParams(HashMap<String, String> customParams) {
            this.customParams = customParams;
            return this;
        }
    }

}