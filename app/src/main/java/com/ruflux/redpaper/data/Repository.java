package com.ruflux.redpaper.data;

import android.content.Context;

import com.ruflux.redpaper.data.local.LocalSource;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.data.remote.RemoteSource;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Repository implements BaseRepository {

    private static Repository INSTANCE = null;
    private final RemoteSource mRemoteSource;
    private final LocalSource mLocalSource;

    private boolean mCacheIsDirty = false;

    private Repository(Context context) {
        mRemoteSource = new RemoteSource();
        mLocalSource = new LocalSource(context);
    }

    public static Repository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new Repository(context);
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<Post>> getPosts(final String sub) {
        return mLocalSource.getPostsFrom(sub)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<List<Post>, ObservableSource<List<Post>>>() {
                    @Override
                    public ObservableSource<List<Post>> apply(@NonNull List<Post> posts) throws Exception {
                        if (posts.isEmpty() || mCacheIsDirty) {
                            mCacheIsDirty = false;
                            return mRemoteSource.requestPosts(sub)
                                    .doOnNext(new Consumer<List<Post>>() {
                                        @Override
                                        public void accept(List<Post> posts) throws Exception {
                                            mLocalSource.savePostsSingleTransaction(sub, posts);
                                        }
                                    });
                        } else
                            return mLocalSource.getPostsFrom(sub);
                    }
                })
                .observeOn(Schedulers.io());
    }

    @Override
    public void refreshPosts() {
        mCacheIsDirty = true;
    }
}
