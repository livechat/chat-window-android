package com.livechatinc.chatwidget.src.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val name: String? = null
) {
    override fun toString(): String {
        return "Author{" +
                "name='" + name + '\'' +
                '}'
    }
}
