package com.tz.sanga.moviestore.Database;

public class MovieObjects {
    private String title;
    private String path;
    private String overview;

    public MovieObjects(String title, String path, String overview) {
        this.title = title;
        this.path = path;
        this.overview = overview;
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
