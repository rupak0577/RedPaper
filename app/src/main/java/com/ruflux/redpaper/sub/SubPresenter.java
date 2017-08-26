package com.ruflux.redpaper.sub;

import android.widget.Toast;

import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.data.remote.RemoteSource;

import java.lang.ref.WeakReference;
import java.util.List;

public class SubPresenter implements SubContract.Presenter {

    private WeakReference<SubContract.View> mView;
    private RemoteSource mRemoteSource;

    public SubPresenter() {
        mRemoteSource = RemoteSource.getRemoteSource();
    }

    @Override
    public void loadPosts() {
        mView.get().startLoadProgress();
        mRemoteSource.requestPosts(mView.get().getPage(), this);
    }

    @Override
    public void notifyLoadSuccess(List<Post> posts) {
        mView.get().stopLoadProgress();
        mView.get().showPosts(posts);
    }

    @Override
    public void notifyLoadFailure(int statusCode) {
        mView.get().stopLoadProgress();
        Toast.makeText(mView.get().getActivityContext(), "Error " + statusCode, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void attachTo(SubContract.View view) {
        mView = new WeakReference<SubContract.View>(view);
        mView.get().attachPresenter(this);
    }

    @Override
    public void start() {
        loadPosts();
    }

    @Override
    public void stop() {
        mRemoteSource.cancel();
    }
}
