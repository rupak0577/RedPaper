package com.ruflux.redpaper.data.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Post {

    private String title;
    private String url;
    private String thumbnailUrl;
    private String id;
    private String domain;

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getId() {
        return id;
    }

    public String getDomain() {
        return domain;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public static ArrayList<Post> fromJson(JSONObject model) {
        ArrayList<Post> mModel = new ArrayList<>();
        Post m = null;
        try {
            JSONArray children = model.getJSONArray("children");
            for (int i = 0; i < children.length(); ++i) {
                JSONObject child = children.getJSONObject(i);
                JSONObject innerData = child.getJSONObject("data");

                if (innerData.getString("domain").startsWith("s"))
                    continue;
                m = new Post();
                m.title = innerData.getString("title");
                m.url = innerData.getString("url");
                m.id = innerData.getString("id");
                m.domain = innerData.getString("domain");
                m.thumbnailUrl = innerData.getString("thumbnail");
                mModel.add(m);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mModel;
    }

}
