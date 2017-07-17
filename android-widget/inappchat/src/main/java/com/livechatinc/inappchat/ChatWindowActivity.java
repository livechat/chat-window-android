package com.livechatinc.inappchat;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Łukasz Jerciński on 10/02/2017.
 */

public final class ChatWindowActivity extends FragmentActivity {
    public static final String KEY_LICENCE_NUMBER = "KEY_LICENCE_NUMBER";
    public static final String KEY_GROUP_ID = "KEY_GROUP_ID";
    public static final String KEY_VISITOR_NAME = "KEY_VISITOR_NAME";
    public static final String KEY_VISITOR_EMAIL = "KEY_VISITOR_EMAIL";

    private static final HashSet<String> DEFINED_KEYS = new HashSet<String>();

    static {
        DEFINED_KEYS.add(KEY_LICENCE_NUMBER);
        DEFINED_KEYS.add(KEY_GROUP_ID);
        DEFINED_KEYS.add(KEY_VISITOR_NAME);
        DEFINED_KEYS.add(KEY_VISITOR_EMAIL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        int frameId = 101;

        LinearLayout linearLayout = new LinearLayout(this);
        FrameLayout frameLayout = new FrameLayout(this);
        //noinspection ResourceType
        frameLayout.setId(frameId);

        linearLayout.addView(frameLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(linearLayout);

        Object licenceNumber = null;
        Object groupId = null;
        String visitorName = null;
        String visitorEmail = null;
        HashMap<String, String> customVariables = new HashMap<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            licenceNumber = String.valueOf(extras.get(KEY_LICENCE_NUMBER));
            groupId = String.valueOf(extras.get(KEY_GROUP_ID));

            if (extras.containsKey(KEY_VISITOR_NAME)) {
                visitorName = String.valueOf(extras.get(KEY_VISITOR_NAME));
            }

            if (extras.containsKey(KEY_VISITOR_EMAIL)) {
                visitorEmail = String.valueOf(extras.get(KEY_VISITOR_EMAIL));
            }
            for (String key : extras.keySet()) {
                if (!DEFINED_KEYS.contains(key)) {
                    customVariables.put(key, String.valueOf(extras.get(key)));
                }
            }
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //noinspection ResourceType
        ft.replace(frameId, ChatWindowFragment.newInstance(licenceNumber, groupId, visitorName, visitorEmail, customVariables));
        ft.commit();
    }
}