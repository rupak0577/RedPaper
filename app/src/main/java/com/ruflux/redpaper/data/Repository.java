package com.ruflux.redpaper.data;

import com.ruflux.redpaper.data.local.LocalSource;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.data.model.SubData;
import com.ruflux.redpaper.data.remote.RedditApi;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class Repository implements BaseRepository {

    private final RedditApi mRemoteSource;
    private final LocalSource mLocalSource;

    public Repository(LocalSource localSource, RedditApi redditApi) {
        mRemoteSource = redditApi;
        mLocalSource = localSource;
    }

    @Override
    public Single<List<Post>> getPosts(final String sub) {
        return refreshPosts(sub);
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
