package com.ruflux.redpaper.data;

import com.ruflux.redpaper.data.model.Post;

import java.util.List;

import io.reactivex.Observable;

public interface BaseRepository {

    Observable<List<Post>> getPosts(String sub);
}
