package com.ruflux.redpaper.data;

import com.ruflux.redpaper.data.model.Post;

import java.util.List;

public interface BaseRepository {

    interface LoadPostsCallback {
        void success(List<Post> posts);

        void failure(int statusCode);
    }

    void getPosts(int contentPage, LoadPostsCallback callback);

    void getPost();

    void refreshPosts();

    void cancel();
}
