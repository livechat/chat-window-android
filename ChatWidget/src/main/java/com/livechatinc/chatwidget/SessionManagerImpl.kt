package com.livechatinc.chatwidget

import com.livechatinc.chatwidget.src.SessionManager
import com.livechatinc.chatwidget.src.common.LiveChatUtils

class SessionManagerImpl : SessionManager {
    override fun clearSession() {
        LiveChatUtils.clearSession()
    }
}
