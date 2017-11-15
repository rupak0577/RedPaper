package com.ruflux.redpaper.di.data;

import android.app.Application;

import com.ruflux.redpaper.data.local.DbHelper;
import com.ruflux.redpaper.data.local.LocalSource;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;

@Module
public class LocalModule {

    @Provides
    @Singleton
    LocalSource provideLocalSource(BriteDatabase sqlBriteDb) {
        return new LocalSource(sqlBriteDb);
    }

    @Provides
    @Singleton
    BriteDatabase providesBriteDb(DbHelper dbHelper) {
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        return sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());
    }

    @Provides
    @Singleton
    DbHelper providesDbHelper(Application context) {
        return new DbHelper(context);
    }
}
