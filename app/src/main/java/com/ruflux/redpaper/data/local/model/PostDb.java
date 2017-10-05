package com.ruflux.redpaper.data.local.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PostDb implements PostModel {
    public static final Creator<PostDb> CREATOR = new Creator<PostDb>() {
        @Override
        public PostDb create(@NonNull String post_id, @NonNull String domain, @NonNull String title,
                             @NonNull String url, @Nullable Boolean is_self, @NonNull String filename,
                             @NonNull String thumbnail_url, @NonNull String preview_url,
                             long height, long width,
                             @NonNull String belongs_to_sub) {
            return new AutoValue_PostDb(post_id, domain, title, url, is_self, filename,
                    thumbnail_url, preview_url, height, width, belongs_to_sub);
        }
    };

    public static final Factory<PostDb> FACTORY = new Factory<>(CREATOR);
    public static final Mapper<PostDb> MAPPER = new Mapper<>(FACTORY);
}
