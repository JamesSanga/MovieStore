package com.tz.sanga.moviestore.Database.Local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert
    void insert(FavoriteNote favoriteNote);

    @Update
    void update(FavoriteNote favoriteNote);

    @Delete
    void delete(FavoriteNote favoriteNote);

    @Query("DELETE FROM favorite")
    void deleteAllFavorites();

    @Query("SELECT EXISTS(SELECT 1 FROM Favorite WHERE id =:id)")
    int checkFavorite(int id);

    @Query("SELECT * FROM favorite ORDER BY id DESC")
    LiveData<List<FavoriteNote>> getAllFavorites();

    @Query("SELECT * FROM favorite WHERE path ==:path")
    LiveData<List<FavoriteNote>> getPath(String path);
}