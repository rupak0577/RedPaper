package com.ruflux.redpaper.data;

import com.ruflux.redpaper.data.local.LocalSource;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.data.model.SubData;
import com.ruflux.redpaper.data.remote.RemoteSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Repository implements BaseRepository {

    private final int VARIANT = 2;

    private static Repository INSTANCE = null;
    private final RemoteSource mRemoteSource;
    private final LocalSource mLocalSource;
    private CompositeDisposable mDisposable;

    private boolean mCacheIsDirty = false;
    private HashMap<String,List<Post>> mCachedPosts;

    private Repository() {
        mRemoteSource = new RemoteSource();
        mLocalSource = new LocalSource();
        mCachedPosts = new HashMap<>();
        mDisposable = new CompositeDisposable();
    }

    public static Repository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Repository();
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<Post>> getPosts(final String sub) {
        if (!mCacheIsDirty && isPageCached(sub))
            return Observable.just(mCachedPosts.get(sub));

        mCacheIsDirty = false;
        Observable<SubData> subData = mRemoteSource.requestPosts(sub);
        return subData.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<SubData, List<Post>>() {
                    @Override
                    public List<Post> apply(@NonNull SubData subData) throws Exception {
                        List<Post> posts = getListOfPosts(subData);
                        mCachedPosts.put(sub, posts);

                        return posts;
                    }
                });
    }

    @Override
    public void refreshPosts() {
        mCacheIsDirty = true;
    }

    private boolean isPageCached(String sub) {
        return mCachedPosts.containsKey(sub);
    }

    private List<Post> getListOfPosts(SubData subData) {
        List<Post> posts = new ArrayList<Post>();
        Post post;

        for (SubData.Child item : subData.getData().getChildren()) {
            if (item.getData().getIsSelf() || item.getData().getDomain().equals("reddit.com"))
                continue;
            post = new Post();

            post.setId(item.getData().getId());
            post.setTitle(item.getData().getTitle());
            post.setThumbnailUrl(item.getData().getPreview().getImages()
                    .get(0).getResolutions().get(VARIANT).getUrl().replaceAll("&amp;", "&"));
            post.setIsSelf(item.getData().getIsSelf());
            post.setPreview(item.getData().getPreview());

            // Trim domain text
            String dom = item.getData().getDomain();
            if (dom.length() > 12)
                post.setDomain(dom.substring(0, 6) + "..." + dom.substring(dom.length()-6, dom.length()));
            else
                post.setDomain(dom);

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
}
