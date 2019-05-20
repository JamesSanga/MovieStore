package com.tz.sanga.moviestore.Database.DataSource;


import android.arch.lifecycle.LiveData;

import com.tz.sanga.moviestore.Database.Local.FavoriteDao;
import com.tz.sanga.moviestore.Database.Local.FavoriteNote;

import java.util.List;

public class FavoriteDataSource implements InterfaceFavoriteDataSource{

    private static FavoriteDataSource instance;
    private FavoriteDao favoriteDao;

    public FavoriteDataSource(FavoriteDao favoriteDao) {
        this.favoriteDao = favoriteDao;
    }

    public static FavoriteDataSource getInstance(FavoriteDao favoriteDao){
        if (instance == null){
            instance = new FavoriteDataSource(favoriteDao);
        }return instance;
    }

    @Override
    public int checkFavorite(int id) {
        return favoriteDao.checkFavorite(id);
    }

    @Override
    public LiveData<List<FavoriteNote>> getPath(String path) {
        return favoriteDao.getPath(path);
    }
}
