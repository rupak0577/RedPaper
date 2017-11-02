package com.ruflux.redpaper.data;

import com.ruflux.redpaper.data.model.Post;

import java.util.List;

import io.reactivex.Single;

public interface BaseRepository {

    Single<List<Post>> getPosts(String sub);
}
