package com.ruflux.redpaper.mvp;

public interface Mvp {

    interface BaseView<T> {
        void attachPresenter(T presenter);
    }

    interface BasePresenter {
        void start();

        void stop();
    }
}
