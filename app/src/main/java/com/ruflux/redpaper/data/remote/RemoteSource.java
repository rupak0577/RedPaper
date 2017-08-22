package com.ruflux.redpaper.data.remote;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.sub.SubPresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RemoteSource {

    private final String TAG = "REMOTE_SOURCE";
    private final String URL_EARTH = "https://www.reddit.com/r/EarthPorn/hot.json";
    private final String URL_ROAD = "https://www.reddit.com/r/RoadPorn/hot.json";
    private final String URL_RURAL = "https://www.reddit.com/r/RuralPorn/hot.json";
    private final String URL_ABANDONED = "https://www.reddit.com/r/AbandonedPorn/hot.json";

    private List<Post> earthPosts = null;
    private List<Post> roadPosts = null;
    private List<Post> ruralPosts = null;
    private List<Post> abandonedPosts = null;
    private AsyncHttpClient client;
    private SubPresenter mSubPresenter;
    private static RemoteSource mRemoteSource;

    private RemoteSource(SubPresenter presenter) {
        this.mSubPresenter = presenter;

        earthPosts = new ArrayList<>();
        roadPosts = new ArrayList<>();
        ruralPosts = new ArrayList<>();
        abandonedPosts = new ArrayList<>();
    }

    public static RemoteSource getRemoteSource(SubPresenter presenter) {
        if (mRemoteSource == null)
            mRemoteSource = new RemoteSource(presenter);

        return mRemoteSource;
    }

    public void requestPosts(final int page) {
        client = new AsyncHttpClient();
        client.setTimeout(3000);
        String URL = "";
        switch (page) {
            case 0:
                URL = URL_EARTH;
                break;
            case 1:
                URL = URL_ROAD;
                break;
            case 2:
                URL = URL_RURAL;
                break;
            case 3:
                URL = URL_ABANDONED;
        }
        client.get(URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                JSONObject jsonData;
                try {
                    jsonData = response.getJSONObject("data");

                    switch (page) {
                        case 0:
                            earthPosts = Post.fromJson(jsonData);
                            mSubPresenter.notifyPostsLoaded(earthPosts);
                            break;
                        case 1:
                            ruralPosts = Post.fromJson(jsonData);
                            mSubPresenter.notifyPostsLoaded(ruralPosts);
                            break;
                        case 2:
                            roadPosts = Post.fromJson(jsonData);
                            mSubPresenter.notifyPostsLoaded(roadPosts);
                            break;
                        case 3:
                            abandonedPosts = Post.fromJson(jsonData);
                            mSubPresenter.notifyPostsLoaded(abandonedPosts);
                            break;
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error in parsing JSON. Initializing <posts> to empty");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(TAG, "Failed to fetch data. <posts> is set to null");

                mSubPresenter.notifyLoadFailure(statusCode);
            }
        });
    }

    public void cancel() {
        if (client != null)
            client.cancelAllRequests(true);
    }

    public List<Post> getPosts(int page) {
        switch (page) {
            case 0:
                return earthPosts;
            case 1:
                return roadPosts;
            case 2:
                return ruralPosts;
            case 3:
                return abandonedPosts;
        }

        return new ArrayList<>();
    }
}
