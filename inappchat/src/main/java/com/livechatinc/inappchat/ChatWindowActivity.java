package com.livechatinc.inappchat;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

public final class ChatWindowActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        int frameId = 101;

        LinearLayout linearLayout = new LinearLayout(this);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(frameId);

        linearLayout.addView(frameLayout, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));

        setContentView(linearLayout);


        final ChatWindowConfiguration chatConfig =
                ChatWindowConfiguration.fromBundle(Objects.requireNonNull(getIntent().getExtras()));

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(frameId,
                ChatWindowFragment.newInstance(
                        chatConfig.licenceNumber,
                        chatConfig.groupId,
                        chatConfig.visitorName,
                        chatConfig.visitorEmail,
                        chatConfig.customParameters
                )
        );

        ft.commit();
    }
}
