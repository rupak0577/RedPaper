package com.ruflux.redpaper.di.sub;

import com.ruflux.redpaper.MainActivity;
import com.ruflux.redpaper.di.AppModule;
import com.ruflux.redpaper.di.data.LocalModule;
import com.ruflux.redpaper.di.data.RemoteModule;
import com.ruflux.redpaper.di.data.RepositoryModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {SubModule.class, SubContractViewModule.class, RepositoryModule.class,
        RemoteModule.class, LocalModule.class, AppModule.class})
public interface SubComponent {
    void inject(MainActivity mainActivity);
}
