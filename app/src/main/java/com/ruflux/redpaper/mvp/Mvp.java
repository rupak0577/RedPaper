package com.ruflux.redpaper.mvp;

import android.content.Context;

public interface Mvp {

    interface BaseModel {

    }

    interface BaseView<T> {
        void setPresenter(T presenter);

        Context getActivityContext();
    }

    interface BasePresenter {
        void start();

        void stop();
    }
}
