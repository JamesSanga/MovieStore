package com.tz.sanga.moviestore.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerResponse {

    @SerializedName("id")
    private int trailerId;
    @SerializedName("results")
    private List<Trailer> results;

    public int getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(int trailerId) {
        this.trailerId = trailerId;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }
}
