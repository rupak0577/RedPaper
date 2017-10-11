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

public class Repository implements BaseRepository {

    private static Repository INSTANCE = null;
    private final RemoteSource mRemoteSource;
    private final LocalSource mLocalSource;

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
                .publish(new Function<Observable<List<Post>>, ObservableSource<List<Post>>>() {
                    @Override
                    public ObservableSource<List<Post>> apply(@NonNull Observable<List<Post>> localObservable) throws Exception {
                        return Observable.merge(localObservable, localObservable.takeUntil(refreshPosts(sub)))
                                .onErrorResumeNext(localObservable);
                    }
                });
    }

    private Observable<List<Post>> refreshPosts(final String sub) {
        return mRemoteSource.requestPosts(sub)
                .doOnNext(new Consumer<List<Post>>() {
                    @Override
                    public void accept(List<Post> posts) throws Exception {
                        mLocalSource.savePostsSingleTransaction(sub, posts);
                    }
                });
    }
}
