package com.livechatinc.chatwidget.src.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChatMessage {
    @SerializedName("text")
    @Expose
    private val text: String? = null

    @SerializedName("id")
    @Expose
    private val id: String? = null

    @SerializedName("timestamp")
    @Expose
    private val timestamp: String? = null

    @SerializedName("author")
    @Expose
    private val author: Author? = null


    override fun toString(): String {
        return "ChatMessage(\n" +
                "text=$text,\n" +
                "id=$id,\n" +
                "timestamp=$timestamp,\n" +
                "author=$author\n" +
                ")"
    }


}
