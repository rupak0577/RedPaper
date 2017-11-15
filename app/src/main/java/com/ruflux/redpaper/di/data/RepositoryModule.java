package com.ruflux.redpaper.di.data;

import com.ruflux.redpaper.data.Repository;
import com.ruflux.redpaper.data.local.LocalSource;
import com.ruflux.redpaper.data.remote.RedditApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    Repository provideRepository(LocalSource localSource, RedditApi redditApi) {
        return new Repository(localSource, redditApi);
    }
}
