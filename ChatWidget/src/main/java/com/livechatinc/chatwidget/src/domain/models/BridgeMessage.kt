package com.livechatinc.chatwidget.src.domain.models

import kotlinx.serialization.Serializable

@Serializable
internal data class BridgeMessage(val messageType: MessageType)
