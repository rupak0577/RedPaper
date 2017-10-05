package com.ruflux.redpaper.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ruflux.redpaper.data.local.model.PostModel;
import com.ruflux.redpaper.data.local.model.SubModel;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "RedPaper.db";
    public static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(SubModel.CREATE_TABLE);
            db.execSQL(PostModel.CREATE_TABLE);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            // Log
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SubModel.DELETEALLSUBS);
        db.execSQL(PostModel.DELETEALLPOSTS);

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
