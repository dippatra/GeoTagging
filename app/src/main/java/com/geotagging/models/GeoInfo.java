package com.geotagging.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "geoInfo")
public class GeoInfo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "lat")
    private double lat;
    @ColumnInfo(name = "lon")
    private double lon;
    @ColumnInfo(name = "image_url")
    private String imageUrl;

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
