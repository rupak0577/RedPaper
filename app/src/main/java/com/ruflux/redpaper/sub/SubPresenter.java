package com.ruflux.redpaper.sub;

import com.ruflux.redpaper.data.BaseRepository;
import com.ruflux.redpaper.data.Repository;
import com.ruflux.redpaper.data.model.Post;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SubPresenter implements SubContract.Presenter {

    private WeakReference<SubContract.View> mView;
    private Repository mRepository;

    private int contentPage;

    public SubPresenter(SubFragment subFragment) {
        mView = new WeakReference<SubContract.View>(subFragment);
        mView.get().attachPresenter(this);
        mRepository = Repository.getInstance();
    }

    @Override
    public void loadPosts(boolean refresh) {
        final SubContract.View fragment = mView.get();
        if (fragment != null) {
            fragment.startLoadProgress();

            if (refresh)
                mRepository.refreshPosts();
            mRepository.getPosts(contentPage, new BaseRepository.LoadPostsCallback() {

                @Override
                public void success(List<Post> posts) {
                    fragment.stopLoadProgress();
                    fragment.showPosts(posts);
                }

                @Override
                public void failure(int statusCode) {
                    fragment.stopLoadProgress();
                    // Init with empty data as we couldn't fetch either from remote or cache
                    fragment.showPosts(new ArrayList<Post>());
                    fragment.showLoadError();
                }
            });
        }
    }

    @Override
    public void start() {
        loadPosts(false);
    }

    @Override
    public void stop() {
        mRepository.cancel();
    }

    public void setSub(int sub) {
        this.contentPage = sub;
    }

    public void isConnected(boolean value) {
        mRepository.isConnected(value);
    }
}
