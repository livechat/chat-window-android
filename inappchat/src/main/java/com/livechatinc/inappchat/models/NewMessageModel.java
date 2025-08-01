package com.livechatinc.inappchat.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewMessageModel {
    @SerializedName("messageType")
    @Expose
    private String messageType;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("author")
    @Expose
    private Author author;

    public String getMessageType() {
        return messageType;
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Author getAuthor() {
        return author;
    }

    @NonNull
    @Override
    public String toString() {
        return "NewMessageModel{" +
                "messageType='" + messageType + '\'' +
                ", text='" + text + '\'' +
                ", id='" + id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", author=" + author +
                '}';
    }
}
