package com.livechatinc.livechatwidgetexample;

import static com.livechatinc.inappchat.ChatWindowConfiguration.KEY_CHAT_WINDOW_CONFIG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.livechatinc.chatsdk.LiveChat;
import com.livechatinc.chatsdk.src.domain.models.IdentityGrant;
import com.livechatinc.inappchat.ChatWindowActivity;
import com.livechatinc.inappchat.ChatWindowConfiguration;

import java.util.Collections;

import kotlin.Unit;

public class LegacyActivity extends AppCompatActivity {
    private final Gson gson = new Gson();

    String licenseNumber = BuildConfig.LICENSE == null ? "1520" : BuildConfig.LICENSE;
    ChatWindowConfiguration windowConfig = new ChatWindowConfiguration.Builder()
            .setLicenceNumber(licenseNumber)
            .build();

    TextView licenseInfoTv;
    ActivityResultLauncher<Intent> editConfigActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legacy);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        licenseInfoTv = findViewById(R.id.license_info);
        licenseInfoTv.setText(windowConfig.toString());

        editConfigActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        windowConfig = (ChatWindowConfiguration.fromBundle(data.getExtras()));
                        licenseInfoTv.setText(windowConfig.toString());
                    }
                });

        LiveChat.getInstance().setErrorListener(
                error -> {
                    Log.e("MainActivity", "### LiveChat error: " + error, error);
                }
        );

        LiveChat.getInstance().setNewMessageListener(
                (newMessage, isChatShown) -> {
                    Log.i("MainActivity", "### new message: " + newMessage.getText());
                }
        );
    }

    private void enableCIP() {
        LiveChat.getInstance().configureIdentityProvider(
                BuildConfig.LICENSE_ID,
                BuildConfig.CLIENT_ID,
                identityGrant -> {
//                    saveCookieGrantToPreferences(cookieGrant);
                    Log.i("UsingKotlinActivity", "### new cookie grant: " + identityGrant);
                    return Unit.INSTANCE;
                }
        );
        final IdentityGrant identityRestorationGrant = readTokenCookiesFromPreferences();

        LiveChat.getInstance().logInCustomer(identityRestorationGrant);
    }

    private IdentityGrant readTokenCookiesFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        final String cookieGrant = sharedPreferences.getString("cookieGrant", null);
        if (cookieGrant == null) {
            return null;
        }

        return gson.fromJson(cookieGrant, IdentityGrant.class);
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
        LiveChat.getInstance().signOutCustomer();
    }

    public void startKotlinVersion(View view) {
        final Intent intent = new Intent(this, UsingKotlinActivity.class);
        startActivity(intent);
    }

    public void startFromSingleton(View view) {
        LiveChat.getInstance().setCustomerInfo(
                "Joe",
                "joe@mail.com",
                "0",
                Collections.singletonMap("internalId", "ABC123")
        );

        LiveChat.getInstance().show(this);
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }
}
