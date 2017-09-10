package com.ruflux.redpaper.sub;

import android.widget.Toast;

import com.ruflux.redpaper.data.BaseRepository;
import com.ruflux.redpaper.data.Repository;
import com.ruflux.redpaper.data.model.Post;

import java.lang.ref.WeakReference;
import java.util.List;

public class SubPresenter implements SubContract.Presenter {

    private WeakReference<SubContract.View> mView;
    private Repository mRepository;

    private int contentPage;
    private boolean isConnected;

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

            if (refresh && isConnected)
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
                    Toast.makeText(fragment.getActivityContext(), "Error " + statusCode, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    @Override
    public void start() {
        loadPosts(isFirstTime());
    }

    @Override
    public void stop() {
        mRepository.cancel();
    }

    private boolean isFirstTime() {
        return !mRepository.isPageCached(contentPage);
    }

    public void loadPage(int page) {
        this.contentPage = page;
        stop();
        loadPosts(isFirstTime());
    }

    public void isConnected(boolean value) {
        this.isConnected = value;

        if (!value)
            mRepository.cancel();
    }
}
