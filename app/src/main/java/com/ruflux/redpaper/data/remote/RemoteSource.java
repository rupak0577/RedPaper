package com.ruflux.redpaper.data.remote;

import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.data.model.SubData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class RemoteSource {

    private RedditApi mRedditApi;

    public RemoteSource(RedditApi redditApi) {
        mRedditApi = redditApi;
    }

    public Observable<List<Post>> getPostsFrom(String sub) {
        return mRedditApi.postsFromSub(sub).map(response -> {
            List<Post> posts = new ArrayList<>();
            Post post;

            for (SubData.Child item : response.getData().getChildren()) {
                if (item.getData().getDomain().startsWith("self") || item.getData().getDomain().equals("reddit.com"))
                    continue;
                post = new Post();

                post.setId(item.getData().getId());
                post.setTitle(item.getData().getTitle());
                post.setDomain(item.getData().getDomain());
                post.setUrl(item.getData().getUrl());
                post.setPreview(item.getData().getPreview());

                posts.add(post);
            }

            return posts;
        }).toObservable();

    }
}
