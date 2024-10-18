package com.livechatinc.livechatwidgetexample;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class UsingKotlinActivity extends Activity {
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.chat_window_kotlin_example);
   }
}
