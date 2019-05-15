package com.tz.sanga.moviestore.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.tz.sanga.moviestore.Model.FavoriteNote;
import com.tz.sanga.moviestore.Repository.FavoriteRepository;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {

    private FavoriteRepository favoriteRepository;
    private LiveData<List<FavoriteNote>> allFavorites;
    private LiveData<List<FavoriteNote>> allFiltered;

    public FavoriteViewModel(@NonNull Application application, String favoriteNote) {
        super(application);
        favoriteRepository = new FavoriteRepository(application, favoriteNote);
        allFavorites = favoriteRepository.getAllFavorites();
        allFiltered = favoriteRepository.checkFavorite();
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

    public LiveData<List<FavoriteNote>>checkFavorite(){
        return allFiltered;
    }
}
