package com.ruflux.redpaper.data.remote;

import com.ruflux.redpaper.data.model.SubData;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface RedditClient {

    @GET("{sub}/hot.json")
    Observable<SubData> postsFromSub(@Path("sub") String sub);
}
