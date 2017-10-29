package com.ruflux.redpaper.data;

import android.content.Context;

import com.ruflux.redpaper.data.local.LocalSource;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.data.model.SubData;
import com.ruflux.redpaper.data.remote.RedditApi;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Repository implements BaseRepository {

    private static Repository INSTANCE = null;
    private final RedditApi mRemoteSource;
    private final LocalSource mLocalSource;

    @Inject
    public Repository(Context context, RedditApi redditApi) {
        mRemoteSource = redditApi;
        mLocalSource = new LocalSource(context);
    }

    @Override
    public Single<List<Post>> getPosts(final String sub) {
        return mLocalSource.getPostsFrom(sub)
                .publish((Function<Single<List<Post>>, SingleSource<List<Post>>>)
                        localObservable -> Single.merge(localObservable, localObservable.takeUntil(refreshPosts(sub))));
    }

    private Single<List<Post>> refreshPosts(final String sub) {
        return requestPosts(sub)
                .doOnSuccess(posts -> mLocalSource.savePostsSingleTransaction(sub, posts))
                .doOnError(throwable -> new Throwable());
    }

    private Single<List<Post>> requestPosts(String sub) {
        return Single.create(subscriber -> {
            mRemoteSource.postsFromSub(sub)
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {

                        List<Post> posts = new ArrayList<>();
                        Post post;

                        for (SubData.Child item : response.getData().getChildren()) {
                            if (item.getData().getIsSelf() || item.getData().getDomain().equals("reddit.com"))
                                continue;
                            post = new Post();

                            post.setId(item.getData().getId());
                            post.setTitle(item.getData().getTitle());
                            post.setDomain(item.getData().getDomain());
                            post.setUrl(item.getData().getUrl());
                            post.setPreview(item.getData().getPreview());
                            post.setIsSelf(item.getData().getIsSelf());

                            posts.add(post);
                        }

                        subscriber.onSuccess(posts);
                    }, throwable -> {
                        subscriber.onError(new Throwable());
                    });
        });
    }
}
