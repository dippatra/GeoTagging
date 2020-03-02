package com.geotagging.utils;

import android.content.Context;

import androidx.room.Room;

import com.geotagging.models.GeoDatabase;

public class DatabaseClient {
    private Context context;
    private static DatabaseClient mInstance;
    private GeoDatabase appDatabase;
    private DatabaseClient(Context context) {
        this.context = context;

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(context, GeoDatabase.class, "GeoTagging_db").build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(context);
        }
        return mInstance;
    }

    public GeoDatabase getAppDatabase() {
        return appDatabase;
    }


}
