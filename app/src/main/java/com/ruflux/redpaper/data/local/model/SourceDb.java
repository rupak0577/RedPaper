package com.ruflux.redpaper.data.local.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SourceDb implements SourceModel {
    public static final SourceModel.Creator<SourceDb> CREATOR =
            ((post_id, url, width, height) ->
                    new AutoValue_SourceDb(post_id, url, width, height));

    public static final SourceModel.Factory<SourceDb> FACTORY = new SourceModel.Factory<>(CREATOR);
    public static final SourceModel.Mapper<SourceDb> MAPPER = new SourceModel.Mapper<>(FACTORY);
}
