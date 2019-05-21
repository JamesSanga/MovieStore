package com.tz.sanga.moviestore.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.tz.sanga.moviestore.Database.Local.Favorite;
import com.tz.sanga.moviestore.Repositories.FavoriteRepository;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {

    private FavoriteRepository favoriteRepository;
    private LiveData<List<Favorite>> allFavorites;
   // private LiveData<List<Favorite>> Id;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        favoriteRepository = new FavoriteRepository(application);
        allFavorites = favoriteRepository.getAllFavorites();
       // Id = favoriteRepository.getId();
    }
    public void insert(Favorite favorite){
        favoriteRepository.insert(favorite);
    }

    public void update(Favorite favorite){
        favoriteRepository.update(favorite);
    }

    public void delete(Favorite favorite){
        favoriteRepository.delete(favorite);
    }

    public void deleteAll(){
        favoriteRepository.deleteAllFavorites();
    }

//    public LiveData<List<Favorite>>getId(){
//        return Id;
//    }

    public LiveData<List<Favorite>>getAllFavorites(){
        return allFavorites;
    }
}
