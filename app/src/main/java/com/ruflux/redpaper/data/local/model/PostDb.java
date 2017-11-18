package com.ruflux.redpaper.data.local.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PostDb implements PostModel {
    public static final Creator<PostDb> CREATOR =
            (post_id, domain, title, url, belongs_to_sub) ->
                    new AutoValue_PostDb(post_id, domain, title, url, belongs_to_sub);

    public static final Factory<PostDb> FACTORY = new Factory<>(CREATOR);
    public static final Mapper<PostDb> MAPPER = new Mapper<>(FACTORY);
}
