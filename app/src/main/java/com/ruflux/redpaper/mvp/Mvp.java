package com.ruflux.redpaper.mvp;

import android.content.Context;

public interface Mvp {

    interface BaseView<T> {
        void attachPresenter(T presenter);

        Context getActivityContext();
    }

    interface BasePresenter {
        void start();

        void stop();
    }
}
