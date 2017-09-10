package com.ruflux.redpaper.data;


import android.util.Log;

import com.ruflux.redpaper.data.local.LocalSource;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.data.remote.RemoteSource;

import java.util.HashMap;
import java.util.List;

public class Repository implements BaseRepository {

    private String TAG = "REPOSITORY";

    private static Repository INSTANCE = null;
    private final RemoteSource mRemoteSource;
    private final LocalSource mLocalSource;

    private boolean mCacheIsDirty = false;
    private HashMap<Integer,List<Post>> mCachedPosts;

    private Repository() {
        mRemoteSource = new RemoteSource();
        mLocalSource = new LocalSource();
        mCachedPosts = new HashMap<>();
    }

    public static Repository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Repository();
        }
        return INSTANCE;
    }

    @Override
    public void getPosts(final int contentPage, final LoadPostsCallback callback) {
        if (mCacheIsDirty || mCachedPosts.get(contentPage) == null) {
            Log.d(TAG, "Loading PAGE: " + contentPage + " from REMOTE");
            mCacheIsDirty = false;
            mRemoteSource.requestPosts(contentPage, new LoadPostsCallback() {
                @Override
                public void success(List<Post> posts) {
                    mCachedPosts.put(contentPage, posts);
                    callback.success(posts);
                }

                @Override
                public void failure(int statusCode) {
                    if (mCachedPosts.get(contentPage) != null)
                        callback.success(mCachedPosts.get(contentPage));
                    else
                        callback.failure(statusCode);
                }
            });
        } else {
            Log.d(TAG, "Loading PAGE: " + contentPage + " from CACHE");
            callback.success(mCachedPosts.get(contentPage));
        }
    }

    @Override
    public void getPost() {

    }

    @Override
    public void refreshPosts() {
        mCacheIsDirty = true;
    }

    @Override
    public void cancel() {
        mRemoteSource.cancel();
    }

    public boolean isPageCached(int contentPage) {
        return mCachedPosts.containsKey(contentPage);
    }
}
