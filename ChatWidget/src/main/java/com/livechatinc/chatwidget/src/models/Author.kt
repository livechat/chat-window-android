package com.livechatinc.chatwidget.src.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal class Author {
    @SerializedName("name")
    @Expose
    private val name: String? = null

    override fun toString(): String {
        return "Author{" +
                "name='" + name + '\'' +
                '}'
    }
}
