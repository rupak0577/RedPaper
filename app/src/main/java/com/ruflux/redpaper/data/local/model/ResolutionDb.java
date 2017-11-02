package com.ruflux.redpaper.data.local.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ResolutionDb implements ResolutionModel {
    public static final ResolutionModel.Creator<ResolutionDb> CREATOR =
            ((resolution_id, url, width, height) ->
                    new AutoValue_ResolutionDb(resolution_id, url, width, height));

    public static final ResolutionModel.Factory<ResolutionDb> FACTORY = new ResolutionModel.Factory<>(CREATOR);
    public static final ResolutionModel.Mapper<ResolutionDb> MAPPER = new ResolutionModel.Mapper<>(FACTORY);
}
