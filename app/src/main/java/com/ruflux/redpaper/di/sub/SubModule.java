package com.ruflux.redpaper.di.sub;

import com.ruflux.redpaper.data.Repository;
import com.ruflux.redpaper.sub.SubContract;
import com.ruflux.redpaper.sub.SubPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class SubModule {

    @Provides
    SubPresenter provideSubPresenter(SubContract.View view, Repository repository) {
        return new SubPresenter(view, repository);
    }
}
