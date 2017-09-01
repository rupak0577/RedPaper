package com.ruflux.redpaper.data.remote;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruflux.redpaper.data.BaseRepository;
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

    private AsyncHttpClient client;

    public void requestPosts(int page, final BaseRepository.LoadPostsCallback callback) {
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

                    callback.success(Post.fromJson(jsonData));
                } catch (JSONException e) {
                    Log.e(TAG, "Error in parsing JSON : " + e.getCause());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(TAG, "Failed to fetch data. STATUS CODE : " + statusCode);

                callback.failure(statusCode);
            }
        });
    }

    public void cancel() {
        if (client != null)
            client.cancelAllRequests(true);
    }
}
