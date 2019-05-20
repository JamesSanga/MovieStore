package com.tz.sanga.moviestore.Repositories;


import android.arch.lifecycle.LiveData;

import com.tz.sanga.moviestore.Database.DataSource.InterfaceFavoriteDataSource;
import com.tz.sanga.moviestore.Database.Local.FavoriteNote;

import java.util.List;

public class Repository implements InterfaceFavoriteDataSource {

    private static Repository instance;
    private InterfaceFavoriteDataSource dataSource;

    public Repository(InterfaceFavoriteDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static Repository getInstance(InterfaceFavoriteDataSource dataSource){
        if (instance == null){
            instance = new Repository(dataSource);
        }return instance;
    }

    @Override
    public int checkFavorite(int id) {
        return dataSource.checkFavorite(id);
    }

    @Override
    public LiveData<List<FavoriteNote>> getPath(String path) {
        return dataSource.getPath(path);
    }
}
