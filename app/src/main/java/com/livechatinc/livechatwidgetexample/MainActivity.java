package com.livechatinc.livechatwidgetexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.livechatinc.inappchat.ChatWindowActivity;
import com.livechatinc.inappchat.ChatWindowConfiguration;

public class MainActivity extends AppCompatActivity {

    public static final String LIVECHAT_SUPPORT_LICENCE_NR = "1520";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    private void startChatActivity() {
        Intent intent = new Intent(this, ChatWindowActivity.class);
        final ChatWindowConfiguration config = BaseApplication.getChatWindowConfiguration();
        intent.putExtras(config.asBundle());
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
