package com.ruflux.redpaper.data;

import com.ruflux.redpaper.data.local.LocalSource;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.data.remote.RemoteSource;

import java.util.List;

import io.reactivex.Observable;

public class Repository implements BaseRepository {

    private final RemoteSource mRemoteSource;
    private final LocalSource mLocalSource;

    public Repository(LocalSource localSource, RemoteSource remoteSource) {
        mRemoteSource = remoteSource;
        mLocalSource = localSource;
    }

    @Override
    public Observable<List<Post>> fetchPosts(final String sub) {
        if (mLocalSource.isSubEmpty(sub)) {
            return mRemoteSource.getPostsFrom(sub)
                    .doOnNext(posts -> {
                        mLocalSource.savePostsSingleTransaction(sub, posts);
                    });
        } else {
            return mLocalSource.getPostsFrom(sub);
        }
    }
}
