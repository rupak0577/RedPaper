package com.ruflux.redpaper.di.data;

import com.ruflux.redpaper.SubPresenter;
import com.ruflux.redpaper.di.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, RepositoryModule.class, LocalModule.class, RemoteModule.class})
public interface RepositoryComponent {
    void inject(SubPresenter subPresenter);
}
