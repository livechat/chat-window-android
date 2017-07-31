package com.livechatinc.inappchat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by szymonjarosz on 24/07/2017.
 */

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
