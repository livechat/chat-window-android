package com.livechatinc.chatwidget.src.managers

import com.livechatinc.chatwidget.src.common.LiveChatUtils

class SessionManagerImpl : SessionManager {
    override fun clearSession() {
        LiveChatUtils.clearSession()
    }
}
