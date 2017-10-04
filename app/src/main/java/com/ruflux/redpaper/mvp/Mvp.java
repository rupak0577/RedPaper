package com.ruflux.redpaper.mvp;

public interface Mvp {

    interface BaseView<T> {
        void bindPresenter();

        void unbindPresenter();
    }

    interface BasePresenter {
        void start();

        void stop();
    }
}
