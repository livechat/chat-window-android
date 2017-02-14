package com.livechatinc.livechatwidgetexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.livechatinc.inappchat.ChatWindowActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chat:
                Intent intent = new Intent(this, ChatWindowActivity.class);
                intent.putExtra(ChatWindowActivity.KEY_GROUP_ID, "0");
                intent.putExtra(ChatWindowActivity.KEY_LICENCE_NUMBER, "1520");
                intent.putExtra(ChatWindowActivity.KEY_VISITOR_NAME, "LiveChat Widget Example Client");
                intent.putExtra(ChatWindowActivity.KEY_VISITOR_EMAIL, "client@example.com");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
