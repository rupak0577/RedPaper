package com.ruflux.redpaper.post;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

public class Downloader {

    private static Downloader INSTANCE;

    private DownloadManager mDownloadManager;

    private Downloader(Context context) {
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static Downloader getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new Downloader(context);
        return INSTANCE;
    }

    long downloadImage(String url, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        return mDownloadManager.enqueue(request);
    }

    int queryStatus(long refId) {
        DownloadManager.Query taskQuery = new DownloadManager.Query();
        taskQuery.setFilterById(refId);

        Cursor cursor = mDownloadManager.query(taskQuery);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (status == DownloadManager.STATUS_RUNNING)
                return 1;
            else
                return 0;
        } else {
            return 0;
        }
    }
}
