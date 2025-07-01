package com.livechatinc.chatwidget.src.core.managers

import com.livechatinc.chatwidget.src.domain.interfaces.managers.SessionManager
import com.livechatinc.chatwidget.src.utils.LiveChatUtils

internal class SessionManagerImpl : SessionManager {
    override fun clearSession() {
        LiveChatUtils.clearSession()
    }
}
