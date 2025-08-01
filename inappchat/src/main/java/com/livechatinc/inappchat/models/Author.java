package com.livechatinc.inappchat.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Author {
    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                '}';
    }
}
