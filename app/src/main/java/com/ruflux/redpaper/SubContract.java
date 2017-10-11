package com.ruflux.redpaper;

import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.mvp.Mvp;

import java.util.List;

interface SubContract {

    interface Presenter extends Mvp.BasePresenter {
        void loadPosts();
    }

    interface View extends Mvp.BaseView<Presenter> {
        void showPosts(List<Post> posts);

        void startLoadProgress();

        void stopLoadProgress();

        void showLoadError();

        String getSelectedSub();
    }
}
