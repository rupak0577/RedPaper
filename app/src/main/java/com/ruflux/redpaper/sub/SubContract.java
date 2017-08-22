package com.ruflux.redpaper.sub;

import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.mvp.Mvp;

import java.util.List;

interface SubContract {

    interface Presenter extends Mvp.BasePresenter {
        void loadPosts();

        void notifyPostsLoaded(List<Post> posts);

        void notifyLoadFailure(int statusCode);
    }

    interface View extends Mvp.BaseView<Presenter> {
        void showPosts(List<Post> posts);

        int getPage();

        void startLoadProgress();

        void stopLoadProgress();
    }
}
