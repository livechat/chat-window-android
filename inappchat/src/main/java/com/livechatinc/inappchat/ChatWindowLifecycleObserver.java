package com.livechatinc.inappchat;

import android.content.ActivityNotFoundException;
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

class ChatWindowLifecycleObserver implements DefaultLifecycleObserver {

    public ChatWindowLifecycleObserver(
            @NonNull ActivityResultRegistry registry,
            @NonNull ActivityNotFoundCallback activityNotFoundCallback
    ) {
        this.registry = registry;
        this.activityNotFoundCallback = activityNotFoundCallback;
    }

    private final ActivityResultRegistry registry;
    private ActivityResultLauncher<String> getContent;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ActivityResultLauncher<String> getMultipleContent;
    private final MutableLiveData<List<Uri>> resultLiveData = new MutableLiveData<>();
    private final ActivityNotFoundCallback activityNotFoundCallback;

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
                (file) -> resultLiveData.postValue(
                        file != null ? Collections.singletonList(file) : Collections.emptyList()
                )
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
        try {
            getContent.launch("*/*");
        } catch (ActivityNotFoundException exception) {
            activityNotFoundCallback.onActivityNotFoundException();
            resultLiveData.postValue(Collections.emptyList());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void selectFiles() {
        try {
            getMultipleContent.launch("*/*");
        } catch (ActivityNotFoundException exception) {
            activityNotFoundCallback.onActivityNotFoundException();
            resultLiveData.postValue(Collections.emptyList());
        }
    }

    public LiveData<List<Uri>> getResultLiveData() {
        return resultLiveData;
    }
}

interface ActivityNotFoundCallback {
    void onActivityNotFoundException();
}
