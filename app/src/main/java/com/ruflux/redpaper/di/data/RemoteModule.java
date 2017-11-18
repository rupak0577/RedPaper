package com.ruflux.redpaper.di.data;

import android.app.Application;

import com.ruflux.redpaper.data.remote.RedditApi;
import com.ruflux.redpaper.data.remote.RemoteSource;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
public class RemoteModule {

    @Provides
    @Singleton
    RemoteSource provideRemoteSource(RedditApi redditApi) {
        return new RemoteSource(redditApi);
    }

    @Provides
    @Singleton
    RedditApi provideRedditApi(Retrofit retrofit) {
        return retrofit.create(RedditApi.class);
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        String BASE_URL = "https://www.reddit.com/r/";
        return new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {
        return new OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(7000, TimeUnit.MILLISECONDS)
                .cache(cache)
                .build();
    }

    @Provides
    @Singleton
    Cache provideOkHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(application.getCacheDir(), cacheSize);
    }
}
