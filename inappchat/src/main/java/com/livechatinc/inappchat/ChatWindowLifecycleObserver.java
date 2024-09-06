package com.livechatinc.inappchat;

import android.net.Uri;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

public class ChatWindowLifecycleObserver implements DefaultLifecycleObserver {

    public ChatWindowLifecycleObserver(@NonNull ActivityResultRegistry registry) {
        this.registry = registry;
    }

    private final ActivityResultRegistry registry;
    private ActivityResultLauncher<String> getContent;
    private ActivityResultLauncher<String> getMultipleContent;
    private final MutableLiveData<List<Uri>> resultLiveData = new MutableLiveData<>();


    public void onCreate(@NonNull LifecycleOwner owner) {
        //TODO: deal with older versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getContent = registry.register(
                    "chatWindowFileResultRegisterKey",
                    owner,
                    new ActivityResultContracts.GetContent(),
                    (file) -> resultLiveData.postValue(Collections.singletonList(file))
            );
            getMultipleContent = registry.register(
                    "chatWindowMultipleFilesResultRegisterKey",
                    owner,
                    new ActivityResultContracts.GetMultipleContents(),
                    resultLiveData::postValue
            );
        }
    }

    public void selectFile() {
        //TODO: consider using accept type from chrome client callback
        getContent.launch("*/*");
    }

    public void selectFiles() {
        getMultipleContent.launch("*/*");
    }

    public LiveData<List<Uri>> getResultLiveData() {
        return resultLiveData;
    }
}
