package com.livechatinc.livechatwidgetexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.livechatinc.chatwidget.LiveChat
import com.livechatinc.livechatwidgetexample.ui.main.HomeFragment

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace<HomeFragment>(R.id.container)
            }
        }
    }

    fun showChat() {
        LiveChat.getInstance().show(this)
    }

    fun showSettings() {

    }
}
