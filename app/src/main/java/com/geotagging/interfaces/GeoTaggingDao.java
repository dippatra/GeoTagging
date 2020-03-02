package com.geotagging.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.geotagging.models.GeoInfo;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface GeoTaggingDao {

    @Query("SELECT * FROM geoInfo")
    List<GeoInfo> getAllGeoInfo();
    @Insert
    void insert(GeoInfo geoInfo);



}
