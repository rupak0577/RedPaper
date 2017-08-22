package com.ruflux.redpaper.post;

import com.ruflux.redpaper.mvp.Mvp;

interface PostContract {

    interface Presenter extends Mvp.BasePresenter {
        void downloadPost(String imageUrl);
    }

    interface View extends Mvp.BaseView<Presenter> {
        void showDownloadProgress();

        void markDownloaded();
    }
}
