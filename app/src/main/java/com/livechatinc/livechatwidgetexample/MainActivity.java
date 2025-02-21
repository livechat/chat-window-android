package com.livechatinc.livechatwidgetexample;

import static com.livechatinc.inappchat.ChatWindowConfiguration.KEY_CHAT_WINDOW_CONFIG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.livechatinc.inappchat.ChatWindowActivity;
import com.livechatinc.inappchat.ChatWindowConfiguration;
import com.livechatinc.inappchat.ChatWindowUtils;

public class MainActivity extends AppCompatActivity {

    String licenceNumber = "11172412";
    ChatWindowConfiguration windowConfig = new ChatWindowConfiguration.Builder()
            .setLicenceNumber(licenceNumber)
            .build();

    TextView licenceInfoTv;
    ActivityResultLauncher<Intent> editConfigActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        licenceInfoTv = findViewById(R.id.licence_info);
        licenceInfoTv.setText(windowConfig.toString());

        editConfigActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        windowConfig = (ChatWindowConfiguration.fromBundle(data.getExtras()));
                        licenceInfoTv.setText(windowConfig.toString());
                    }
                });
    }


    private void startChatActivity() {
        Intent intent = new Intent(this, ChatWindowActivity.class);
        intent.putExtra(KEY_CHAT_WINDOW_CONFIG, windowConfig);

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        final Fragment fragmentById = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragmentById instanceof OnBackPressedListener && ((OnBackPressedListener) fragmentById).onBackPressed()) {
            //Let fragment handle this
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void startFullScreenWindowExample(View view) {
        final Intent intent = new Intent(this, FullScreenWindowActivityExample.class);
        intent.putExtra(KEY_CHAT_WINDOW_CONFIG, windowConfig);

        startActivity(intent);
    }

    public void startEmbeddedWindowExample(View view) {
        final Fragment fragment = EmbeddedChatWindowFragmentExample.newInstance();
        final Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CHAT_WINDOW_CONFIG, windowConfig);
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .addToBackStack("EmbeddedFragmentExample")
                .commit();
    }

    public void editConfiguration(View view) {
        final Intent intent = new Intent(this, EditConfigurationActivity.class);
        intent.putExtra(KEY_CHAT_WINDOW_CONFIG, windowConfig);

        editConfigActivityResultLauncher.launch(intent);
    }

    public void startOwnActivityExample(View view) {
        startChatActivity();
    }

    public void clearChatSession(View view) {
        ChatWindowUtils.clearSession();
    }

    public void startKotlinVersion(View view) {
        final Intent intent = new Intent(this, UsingKotlinActivity.class);
        startActivity(intent);
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }
}
