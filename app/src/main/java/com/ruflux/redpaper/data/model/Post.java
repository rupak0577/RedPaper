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
    private String fileName;

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

    public String getFileName() {
        return fileName;
    }

    public static ArrayList<Post> fromJson(JSONObject model) {
        ArrayList<Post> mModel = new ArrayList<>();
        Post m;
        try {
            JSONArray children = model.getJSONArray("children");
            for (int i = 0; i < children.length(); ++i) {
                JSONObject child = children.getJSONObject(i);
                JSONObject innerData = child.getJSONObject("data");

                if (innerData.getString("domain").startsWith("r") ||
                        innerData.getString("domain").startsWith("s")) // domain = reddit/self, skip
                    continue;
                m = new Post();
                m.id = innerData.getString("id");
                m.title = innerData.getString("title");
                m.domain = innerData.getString("domain");
                m.url = innerData.getString("url");

                // https://imgur.com/xyz --> https://i.imgur.com/xyz.jpg
                if (m.domain.equals("imgur.com")) {
                    m.url = m.url.substring(0, m.url.indexOf("/") + 2) + "i."
                            + m.url.substring(m.url.indexOf("i")) + ".jpeg";
                }
                m.fileName = m.url.substring(m.url.lastIndexOf("/") + 1);

                JSONObject preview = innerData.getJSONObject("preview");
                JSONArray images = preview.getJSONArray("images");
                JSONArray res = images.getJSONObject(0).getJSONArray("resolutions");

                JSONObject variant = res.getJSONObject(2); // The 3rd variant is the closest
                m.thumbnailUrl = variant.getString("url").replaceAll("&amp;", "&");
                mModel.add(m);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mModel;
    }

}
