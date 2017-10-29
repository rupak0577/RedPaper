package com.ruflux.redpaper.data.remote;

import com.ruflux.redpaper.data.model.SubData;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RedditApi {

    @GET("{sub}/hot.json?raw_json=1")
    Single<SubData> postsFromSub(@Path("sub") String sub);
}
