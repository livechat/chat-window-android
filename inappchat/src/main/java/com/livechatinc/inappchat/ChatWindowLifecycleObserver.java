package com.livechatinc.inappchat;

import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ChatWindowLifecycleObserver implements DefaultLifecycleObserver {

    public ChatWindowLifecycleObserver(@NonNull ActivityResultRegistry registry) {
        this.registry = registry;
    }

    private final ActivityResultRegistry registry;
    private ActivityResultLauncher<String> getContent;
    private final MutableLiveData<Uri> resultLiveData = new MutableLiveData<>();


    public void onCreate(@NonNull LifecycleOwner owner) {
        getContent = registry.register(
                "chatWindowActivityResultRegisterKey",
                owner,
                new ActivityResultContracts.GetContent(),
                resultLiveData::postValue
        );
    }

    public void selectFile() {
        //TODO: consider using accept type from chrome client callback
        getContent.launch("*/*");
    }

    public LiveData<Uri> getResultLiveData() {
        return resultLiveData;
    }
}
