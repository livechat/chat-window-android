package com.livechatinc.inappchat;

import android.content.Intent;

/**
 * Created by szymonjarosz on 20/07/2017.
 */

interface IChatWindowView {
    void showChatWindow();
    void hideChatWindow();
    boolean onBackPressed();
    boolean onActivityResult(int requestCode, int resultCode, Intent data);
    boolean isInitialized();
}
