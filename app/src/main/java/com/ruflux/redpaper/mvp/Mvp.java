package com.ruflux.redpaper.mvp;

import android.content.Context;

public interface Mvp {

    interface BaseView<T> {
        Context fetchContext();
    }

    interface BasePresenter {
        void start();

        void stop();
    }
}
