package com.ruflux.redpaper.di.remote;

import com.ruflux.redpaper.data.remote.RedditApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class RedditModule {

    @Provides
    @Singleton
    RedditApi provideRedditApi(Retrofit retrofit) {
        return retrofit.create(RedditApi.class);
    }
}
