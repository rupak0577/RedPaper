package com.ruflux.redpaper;

import android.app.Application;

import com.ruflux.redpaper.post.Downloader;
import com.squareup.leakcanary.LeakCanary;

public class RedPaperApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    public Downloader getDownloader() {
        return Downloader.getInstance(getApplicationContext());
    }
}
