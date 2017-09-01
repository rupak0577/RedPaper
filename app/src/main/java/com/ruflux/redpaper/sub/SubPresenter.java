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

    public SubPresenter() {
        mRepository = Repository.getInstance();
    }

    @Override
    public void loadPosts(boolean refresh) {
        mView.get().startLoadProgress();

        if (refresh)
            mRepository.refreshPosts();
        mRepository.getPosts(contentPage, new BaseRepository.LoadPostsCallback() {

            @Override
            public void success(List<Post> posts) {
                mView.get().stopLoadProgress();
                mView.get().showPosts(posts);
            }

            @Override
            public void failure(int statusCode) {
                mView.get().stopLoadProgress();
                Toast.makeText(mView.get().getActivityContext(), "Error " + statusCode, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void attachTo(SubContract.View view) {
        mView = new WeakReference<>(view);
        mView.get().attachPresenter(this);
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

    public void setContentPage(int page) {
        this.contentPage = page;
    }
}
