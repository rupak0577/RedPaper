package com.ruflux.redpaper.data.local.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SubDb implements SubModel {
    public static final Creator<SubModel> CREATOR = sub_name -> new AutoValue_SubDb(sub_name);

    public static final Factory<SubModel> FACTORY = new Factory<>(CREATOR);
    public static final Mapper<SubModel> MAPPER = new Mapper<>(FACTORY);
}
