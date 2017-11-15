package com.ruflux.redpaper;

import android.app.Application;

import com.ruflux.redpaper.di.AppModule;
import com.ruflux.redpaper.di.data.DaggerRepositoryComponent;
import com.ruflux.redpaper.di.data.RepositoryComponent;
import com.ruflux.redpaper.di.data.RepositoryModule;
import com.ruflux.redpaper.post.Downloader;
import com.squareup.leakcanary.LeakCanary;

public class RedPaperApplication extends Application {

    private RepositoryComponent repositoryComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        repositoryComponent = DaggerRepositoryComponent.builder()
                .appModule(new AppModule(this))
                .repositoryModule(new RepositoryModule())
                .build();
    }

    public Downloader getDownloader() {
        return Downloader.getInstance(getApplicationContext());
    }

    public RepositoryComponent getRepositoryComponent() {
        return repositoryComponent;
    }
}
