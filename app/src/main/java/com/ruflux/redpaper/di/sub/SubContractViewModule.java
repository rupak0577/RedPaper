package com.ruflux.redpaper.di.sub;

import com.ruflux.redpaper.sub.SubContract;

import dagger.Module;
import dagger.Provides;

@Module
public class SubContractViewModule {

    private SubContract.View mView;

    public SubContractViewModule(SubContract.View mView) {
        this.mView = mView;
    }

    @Provides
    SubContract.View provideSubContractView() {
        return mView;
    }
}
