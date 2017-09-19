package com.ruflux.redpaper.data.remote;

import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.data.model.SubData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
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

    public Observable<List<Post>> requestPosts(final String sub) {
        Observable<SubData> subData = client.postsFromSub(sub);
        return subData.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<SubData, List<Post>>() {
                    @Override
                    public List<Post> apply(@NonNull SubData subData) throws Exception {
                        List<Post> posts = new ArrayList<Post>();
                        Post post;

                        for (SubData.Child item : subData.getData().getChildren()) {
                            if (item.getData().getIsSelf() || item.getData().getDomain().equals("reddit.com"))
                                continue;
                            post = new Post();
                            post.setDomain(item.getData().getDomain());
                            post.setId(item.getData().getId());
                            post.setTitle(item.getData().getTitle());
                            post.setPreview(item.getData().getPreview());

                            // https://imgur.com/xyz --> https://i.imgur.com/xyz.jpeg
                            String url = item.getData().getUrl();
                            if (post.getDomain().equals("imgur.com")) {
                                post.setUrl(url.substring(0, url.indexOf("/") + 2) + "i."
                                        + url.substring(url.indexOf("i")) + ".jpeg");
                            } else
                                post.setUrl(url);
                            post.setFilename(post.getUrl().substring(post.getUrl()
                                    .lastIndexOf("/") + 1));

                            posts.add(post);
                        }

                        return posts;
                    }
                });
    }
}
