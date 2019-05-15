package com.tz.sanga.moviestore.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favorite")
public class FavoriteNote {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String path;
    private String overview;

    public FavoriteNote(String title, String path, String overview) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.overview = overview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
