package com.geotagging.models;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.geotagging.interfaces.GeoTaggingDao;

@Database(entities = GeoInfo.class,exportSchema = false,version = 1)
public abstract class GeoDatabase extends RoomDatabase {
    public abstract GeoTaggingDao geoTaggingDao();

}
