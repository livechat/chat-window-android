package com.livechatinc.livechatwidgetexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.livechatinc.inappchat.ChatWindowActivity;
import com.livechatinc.inappchat.ChatWindowConfiguration;
import com.livechatinc.inappchat.ChatWindowUtils;

public class MainActivity extends AppCompatActivity {

    String licenceNumber = "1520";
    ChatWindowConfiguration windowConfig = new ChatWindowConfiguration.Builder()
            .setLicenceNumber(licenceNumber)
            .build();
    TextView licenceInfoTv;
    ActivityResultLauncher<Intent> someActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        licenceInfoTv = findViewById(R.id.licence_info);
        licenceInfoTv.setText(windowConfig.toString());

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.i("TAG", "coming back from activity" + result.getData());
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            windowConfig = (ChatWindowConfiguration) data.getSerializableExtra("config");
                            licenceInfoTv.setText(windowConfig.toString());
                        }
                    }
                });
    }


    private void startChatActivity() {
        Intent intent = new Intent(this, ChatWindowActivity.class);
        intent.putExtras(windowConfig.asBundle());
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
        final Intent intent = new Intent(this, FullScreenWindowActivityExample.class);
        intent.putExtra("config", windowConfig);
        startActivity(intent);
    }

    public void startEmbeddedWindowExample(View view) {
        final Fragment fragment = EmbeddedChatWindowFragmentExample.newInstance();
        fragment.setArguments(windowConfig.asBundle());
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).addToBackStack("EmbeddedFragmentExample").commit();
    }

    public void editConfiguration(View view) {
        final Intent intent = new Intent(this, EditConfigurationActivity.class);
        intent.putExtra("config", windowConfig);
        someActivityResultLauncher.launch(intent);
    }

    public void startOwnActivityExample(View view) {
        startChatActivity();
    }

    public void clearChatSession(View view) {
        ChatWindowUtils.clearSession(view.getContext());
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

}
