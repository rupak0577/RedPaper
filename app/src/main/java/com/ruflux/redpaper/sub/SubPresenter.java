package com.ruflux.redpaper.sub;

import android.widget.Toast;

import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.data.remote.RemoteSource;

import java.util.List;

public class SubPresenter implements SubContract.Presenter {

    private SubContract.View mView;
    private RemoteSource mRemoteSource;

    public SubPresenter(SubContract.View view) {
        this.mView = view;
        this.mView.setPresenter(this);

        this.mRemoteSource = RemoteSource.getRemoteSource(this);
    }

    @Override
    public void loadPosts() {
        mView.startLoadProgress();
        mRemoteSource.requestPosts(mView.getPage());
    }

    @Override
    public void notifyPostsLoaded(List<Post> posts) {
        mView.stopLoadProgress();
        mView.showPosts(posts);
    }

    @Override
    public void notifyLoadFailure(int statusCode) {
        mView.stopLoadProgress();
        Toast.makeText(mView.getActivityContext(), "Error " + statusCode, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void start() {
        loadPosts();
    }

    @Override
    public void stop() {
        mRemoteSource.cancel();
        mView = null;
    }
}
