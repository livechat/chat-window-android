package com.livechatinc.inappchat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by szymonjarosz on 24/07/2017.
 */

public class Author {
    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                '}';
    }
}
