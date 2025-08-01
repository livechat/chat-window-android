package com.livechatinc.chatsdk.src.core.managers

import com.livechatinc.chatsdk.src.domain.interfaces.managers.SessionManager
import com.livechatinc.chatsdk.src.utils.LiveChatUtils

internal class SessionManagerImpl : SessionManager {
    override fun clearSession() {
        LiveChatUtils.clearSession()
    }
}
