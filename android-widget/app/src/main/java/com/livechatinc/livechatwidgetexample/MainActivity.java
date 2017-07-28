package com.livechatinc.livechatwidgetexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.livechatinc.inappchat.ChatWindowActivity;

public class MainActivity extends AppCompatActivity {

    public static final String LIVECHAT_SUPPORT_LICENCE_NR = "1520";
    public static final String CHATIO_SUPPORT_LICENCE_NR = "8928139";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    private void startChatActivity() {
        Intent intent = new Intent(this, ChatWindowActivity.class);
        intent.putExtra(ChatWindowActivity.KEY_GROUP_ID, "0");
        intent.putExtra(ChatWindowActivity.KEY_LICENCE_NUMBER, LIVECHAT_SUPPORT_LICENCE_NR);
        intent.putExtra(ChatWindowActivity.KEY_VISITOR_NAME, "ChatWindow Example Client");
        intent.putExtra(ChatWindowActivity.KEY_VISITOR_EMAIL, "client@example.com");
        intent.putExtra("myParam", "Android Rules!");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        final Fragment fragmentById = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragmentById != null && fragmentById instanceof OnBackPressedListener && ((OnBackPressedListener) fragmentById).onBackPressed()) {
            //Let fragment handle this
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void startFullScreenWindowExample(View view) {
        startActivity(new Intent(this, FullScreenWindowActivityExample.class));
    }

    public void startEmbeddedWindowExample(View view) {
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, EmbeddedChatWindowFragmentExample.newInstance()).addToBackStack("EmbeddedFragmentExample").commit();
    }

    public void startOwnActivityExample(View view) {
        startChatActivity();
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

}
