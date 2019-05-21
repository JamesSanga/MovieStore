package com.tz.sanga.moviestore.Repositories;


import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.tz.sanga.moviestore.Database.Local.Favorite;
import com.tz.sanga.moviestore.Database.Local.FavoriteDao;
import com.tz.sanga.moviestore.Database.Local.FavoriteDatabase;

import java.util.List;

public class FavoriteRepository {
    private FavoriteDao favoriteDao;
    private LiveData<List<Favorite>>allFavorites;

    public FavoriteRepository(Context application){
        FavoriteDatabase favoriteDatabase = FavoriteDatabase.getInstance(application);
        favoriteDao = favoriteDatabase.favoriteDao();
        allFavorites = favoriteDao.getAllFavorites();
    }

    public void insert(Favorite favorite){
        new FavoriteRepository.InsertFavoriteAsyncTask(favoriteDao).execute(favorite);
    }
    public void update(Favorite favorite){
        new FavoriteRepository.UpdateFavoriteAsyncTask(favoriteDao).execute(favorite);
    }
    public void delete(Favorite favorite){
        new FavoriteRepository.DeleteFavoriteAsyncTask(favoriteDao).execute(favorite);
    }
    public void deleteAllFavorites(){
        new FavoriteRepository.DeleteAllFavoriteAsyncTask(favoriteDao).execute();
    }

    public boolean getPath(String path){
        return favoriteDao.getPath(path);
    }

    public LiveData<List<Favorite>>getAllFavorites(){
        return allFavorites;
    }
    public int deleteByPath(String path){
        return favoriteDao.deleteByPath(path);
    }

    private static class InsertFavoriteAsyncTask extends AsyncTask<Favorite, Void, Void> {
        private FavoriteDao favoriteDao;

        public InsertFavoriteAsyncTask(FavoriteDao favoriteDao) {
            this.favoriteDao = favoriteDao;
        }

        @Override
        protected Void doInBackground(Favorite... favorites) {
            favoriteDao.insert(favorites[0]);
            return null;
        }
    }


    private static class UpdateFavoriteAsyncTask extends AsyncTask<Favorite, Void, Void>{
        private FavoriteDao favoriteDao;

        public UpdateFavoriteAsyncTask(FavoriteDao favoriteDao) {
            this.favoriteDao = favoriteDao;
        }

        @Override
        protected Void doInBackground(Favorite... favorites) {
            favoriteDao.update(favorites[0]);
            return null;
        }
    }

    private static class DeleteFavoriteAsyncTask extends AsyncTask<Favorite, Void, Void>{
        private FavoriteDao favoriteDao;

        public DeleteFavoriteAsyncTask(FavoriteDao favoriteDao) {
            this.favoriteDao = favoriteDao;
        }

        @Override
        protected Void doInBackground(Favorite... favorites) {
            favoriteDao.delete(favorites[0]);
            return null;
        }
    }

    private static class DeleteAllFavoriteAsyncTask extends AsyncTask<Void, Void, Void>{
        private FavoriteDao favoriteDao;

        public DeleteAllFavoriteAsyncTask(FavoriteDao favoriteDao) {
            this.favoriteDao = favoriteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            favoriteDao.deleteAllFavorites();
            return null;
        }
    }
}
