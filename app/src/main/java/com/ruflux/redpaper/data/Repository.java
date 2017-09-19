package com.ruflux.redpaper.data;

import com.ruflux.redpaper.data.local.LocalSource;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.data.model.SubData;
import com.ruflux.redpaper.data.remote.RemoteSource;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class Repository implements BaseRepository {

    private static Repository INSTANCE = null;
    private final RemoteSource mRemoteSource;
    private final LocalSource mLocalSource;
    private CompositeDisposable mDisposable;

    private boolean mCacheIsDirty = false;
    private HashMap<String,List<SubData>> mCachedPosts;
    private boolean isConnected;

    private Repository() {
        mRemoteSource = new RemoteSource();
        mLocalSource = new LocalSource();
        mCachedPosts = new HashMap<>();
        mDisposable = new CompositeDisposable();
    }

    public static Repository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Repository();
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<Post>> getPosts(final String sub) {
        return mRemoteSource.requestPosts(sub)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    @Override
    public void refreshPosts() {
        mCacheIsDirty = true;
    }

    @Override
    public void cancel() {
    }

    public void isConnected(boolean value) {
        this.isConnected = value;
    }

    private boolean isPageCached(String sub) {
        return mCachedPosts.containsKey(sub);
    }
}
