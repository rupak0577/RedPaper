package com.ruflux.redpaper.data.remote;

import com.ruflux.redpaper.data.model.SubData;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RemoteSource {

    private final String BASE_URL = "https://www.reddit.com/r/";

    private RedditClient client;

    public RemoteSource() {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create());
        Retrofit retrofit = builder.client(new OkHttpClient.Builder().build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        client = retrofit.create(RedditClient.class);
    }

    public Observable<SubData> requestPosts(String sub) {
        return client.postsFromSub(sub).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }
}
