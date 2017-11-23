package com.ruflux.redpaper.sub;

import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.mvp.Mvp;

import java.util.List;

public interface SubContract {

    interface Presenter extends Mvp.BasePresenter {
        void loadPosts(String sub);
    }

    interface View extends Mvp.BaseView<Presenter> {
        void showPosts(List<Post> posts);

        void onStartLoadProgress();

        void onStopLoadProgress();

        void onLoadError(String message);
    }
}
