package com.tz.sanga.moviestore.Database.DataSource;


import android.arch.lifecycle.LiveData;

import com.tz.sanga.moviestore.Database.Local.FavoriteNote;

import java.util.List;

public interface InterfaceFavoriteDataSource {

    int checkFavorite(int id);
    LiveData<List<FavoriteNote>> getPath(String path);

}
