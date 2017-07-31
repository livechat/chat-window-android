package com.livechatinc.inappchat;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Created by Łukasz Jerciński on 09/02/2017.
 */

public class UriUtils {
    private UriUtils() {

    }

    public static String getFilePathFromUri(Context context, Uri uri) {
        if (isVersionKitKat() && isDocumentUri(context, uri)) {
            return getFilePathFromDocumentUriKitKat(context, uri);
        } else if (isContentUri(uri)) {
            return getDataColumnForContentUri(context, uri, null, null);
        } else {
            return uri.getPath();
        }
    }

    private static boolean isVersionKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean isDocumentUri(Context context, Uri uri) {
        return DocumentsContract.isDocumentUri(context, uri);
    }

    private static boolean isContentUri(Uri uri) {
        return "content".equalsIgnoreCase(uri.getScheme());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getFilePathFromDocumentUriKitKat(Context context, Uri uri) {
        if (isExternalStorageDocument(uri)) {
            return getFilePathForExternalStorageDocumentUri(uri);
        } else if (isDownloadsDocument(uri)) {
            return getFilePathForDownloadsDocumentUri(context, uri);
        } else if (isMediaDocument(uri)) {
            return getFilePathFromMediaDocumentUri(context, uri);
        } else {
            return uri.getPath();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getFilePathForExternalStorageDocumentUri(Uri uri) {
        String documentId = DocumentsContract.getDocumentId(uri);

        String[] split = documentId.split(":");

        String uriContentType = split[0];
        String uriId = split[1];

        if ("primary".equalsIgnoreCase(uriContentType)) {
            return Environment.getExternalStorageDirectory() + "/" + uriId;
        } else {
            return uri.getPath();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getFilePathForDownloadsDocumentUri(Context context, Uri uri) {
        String documentId = DocumentsContract.getDocumentId(uri);

        Uri downloadsContentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));

        return getDataColumnForContentUri(context, downloadsContentUri, null, null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getFilePathFromMediaDocumentUri(Context context, Uri uri) {
        String documentId = DocumentsContract.getDocumentId(uri);

        String[] split = documentId.split(":");

        String uriContentType = split[0];
        String uriId = split[1];

        Uri contentUri = getUriForContentType(uriContentType);

        String selection = "_id=?";
        String[] selectionArgs = new String[] { uriId };

        return getDataColumnForContentUri(context, contentUri, selection, selectionArgs);
    }

    private static Uri getUriForContentType(String type) {
        switch (type) {
            case "image":
                return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            case "video":
                return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            case "audio":
                return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            default:
                return null;
        }
    }

    public static String getDataColumnForContentUri(Context context, Uri uri, String selection, String[] selectionArgs) {
        String column = "_data";
        String[] projection = { column };

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
