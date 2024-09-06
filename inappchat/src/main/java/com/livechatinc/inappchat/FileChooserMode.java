package com.livechatinc.inappchat;

import android.os.Build;

import androidx.annotation.RequiresApi;

enum FileChooserMode {
    SINGLE,
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    MULTIPLE
}
