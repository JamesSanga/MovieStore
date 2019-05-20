package com.tz.sanga.moviestore.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.tz.sanga.moviestore.Database.Local.FavoriteNote;
import com.tz.sanga.moviestore.Repositories.FavoriteRepository;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {

    private FavoriteRepository favoriteRepository;
    private LiveData<List<FavoriteNote>> allFavorites;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        favoriteRepository = new FavoriteRepository(application);
        allFavorites = favoriteRepository.getAllFavorites();
    }
    public void insert(FavoriteNote favoriteNote){
        favoriteRepository.insert(favoriteNote);
    }

    public void update(FavoriteNote favoriteNote){
        favoriteRepository.update(favoriteNote);
    }

    public void delete(FavoriteNote favoriteNote){
        favoriteRepository.delete(favoriteNote);
    }

    public void deleteAll(){
        favoriteRepository.deleteAllFavorites();
    }

    public LiveData<List<FavoriteNote>>getAllFavorites(){
        return allFavorites;
    }
}
