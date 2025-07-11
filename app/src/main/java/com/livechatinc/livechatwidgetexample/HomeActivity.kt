package com.livechatinc.livechatwidgetexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.livechatinc.chatwidget.LiveChat

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun showChat() {
        LiveChat.getInstance().show(this)
    }
}
