package com.ruflux.redpaper;

import android.app.Application;

//import com.raizlabs.android.dbflow.config.FlowConfig;
//import com.raizlabs.android.dbflow.config.FlowManager;
import com.ruflux.redpaper.post.Downloader;

public class RedPaperApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //FlowManager.init(new FlowConfig.Builder(this).build());
    }

    public Downloader getDownloader() {
        return Downloader.getInstance(getApplicationContext());
    }
}
