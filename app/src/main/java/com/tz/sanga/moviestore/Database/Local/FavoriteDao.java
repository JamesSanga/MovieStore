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
    void insert(Favorite favorite);

    @Update
    void update(Favorite favorite);

    @Delete
    void delete(Favorite favorite);

    @Query("DELETE FROM Favorite")
    void deleteAllFavorites();

    @Query("SELECT * FROM Favorite ORDER BY id DESC")
    LiveData<List<Favorite>> getAllFavorites();

    @Query("DELETE FROM Favorite WHERE path =:path")
    int deleteByPath(String path);

    @Query("SELECT EXISTS(SELECT 1 FROM Favorite WHERE path =:path)")
    boolean getPath(String path);

}
