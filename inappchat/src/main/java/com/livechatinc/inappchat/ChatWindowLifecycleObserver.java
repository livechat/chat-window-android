package com.livechatinc.inappchat;

import android.net.Uri;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ActivityResultLauncher<String> getMultipleContent;
    private final MutableLiveData<List<Uri>> resultLiveData = new MutableLiveData<>();


    public void onCreate(@NonNull LifecycleOwner owner) {
        registerSingleContentContract(owner);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            registerMultipleContentContract(owner);
        }
    }

    private void registerSingleContentContract(@NonNull LifecycleOwner owner) {
        getContent = registry.register(
                "chatWindowFileResultRegisterKey",
                owner,
                new ActivityResultContracts.GetContent(),
                (file) -> resultLiveData.postValue(Collections.singletonList(file))
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void registerMultipleContentContract(@NonNull LifecycleOwner owner) {
        getMultipleContent = registry.register(
                "chatWindowMultipleFilesResultRegisterKey",
                owner,
                new ActivityResultContracts.GetMultipleContents(),
                resultLiveData::postValue
        );
    }

    public void selectFile() {
        //TODO: consider using accept type from chrome client callback
        getContent.launch("*/*");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void selectFiles() {
        getMultipleContent.launch("*/*");
    }

    public LiveData<List<Uri>> getResultLiveData() {
        return resultLiveData;
    }
}
