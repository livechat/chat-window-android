package com.livechatinc.livechatwidgetexample;

import static com.livechatinc.inappchat.ChatWindowConfiguration.KEY_CHAT_WINDOW_CONFIG;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.livechatinc.inappchat.ChatWindowConfiguration;

import java.util.Objects;

public class EditConfigurationActivity extends AppCompatActivity {
    EditText licenseNumber;
    EditText groupId;
    EditText visitorName;
    EditText visitorEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_configuration_activity);

        final ChatWindowConfiguration config =
                ChatWindowConfiguration.fromBundle(Objects.requireNonNull(getIntent().getExtras()));

        licenseNumber = findViewById(R.id.license_number);
        licenseNumber.setText(config.licenceNumber);
        groupId = findViewById(R.id.group_id);
        groupId.setText(config.groupId);
        visitorName = findViewById(R.id.visitor_name);
        visitorName.setText(config.visitorName);
        visitorEmail = findViewById(R.id.visitor_email);
        visitorEmail.setText(config.visitorEmail);
    }

    public void submitConfig(View view) {
        Intent data = new Intent();
        final ChatWindowConfiguration config = new ChatWindowConfiguration.Builder()
                .setLicenceNumber(licenseNumber.getText().toString())
                .setGroupId(groupId.getText().toString())
                .setVisitorName(visitorName.getText().toString())
                .setVisitorEmail(visitorEmail.getText().toString())
                .build();
        data.putExtra(KEY_CHAT_WINDOW_CONFIG, config);
        setResult(RESULT_OK, data);
        finish();
    }
}
