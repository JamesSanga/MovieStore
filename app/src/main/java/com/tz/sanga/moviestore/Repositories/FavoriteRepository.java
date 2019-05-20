package com.tz.sanga.moviestore.Repositories;


import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.tz.sanga.moviestore.Database.Local.FavoriteDao;
import com.tz.sanga.moviestore.Database.Local.FavoriteDatabase;
import com.tz.sanga.moviestore.Database.Local.FavoriteNote;

import java.util.List;

public class FavoriteRepository {
    private FavoriteDao favoriteDao;
    private LiveData<List<FavoriteNote>>allFavorites;

    public FavoriteRepository(Context application){
        FavoriteDatabase favoriteDatabase = FavoriteDatabase.getInstance(application);
        favoriteDao = favoriteDatabase.favoriteDao();
        allFavorites = favoriteDao.getAllFavorites();
    }

    public void insert(FavoriteNote favoriteNote){
        new FavoriteRepository.InsertFavoriteAsyncTask(favoriteDao).execute(favoriteNote);
    }
    public void update(FavoriteNote favoriteNote){
        new FavoriteRepository.UpdateFavoriteAsyncTask(favoriteDao).execute(favoriteNote);
    }
    public void delete(FavoriteNote favoriteNote){
        new FavoriteRepository.DeleteFavoriteAsyncTask(favoriteDao).execute(favoriteNote);
    }
    public void deleteAllFavorites(){
        new FavoriteRepository.DeleteAllFavoriteAsyncTask(favoriteDao).execute();
    }

    public boolean getPath(String path){
        return favoriteDao.getPath(path);
    }

    public LiveData<List<FavoriteNote>>getAllFavorites(){
        return allFavorites;
    }

    private static class InsertFavoriteAsyncTask extends AsyncTask<FavoriteNote, Void, Void> {
        private FavoriteDao favoriteDao;

        public InsertFavoriteAsyncTask(FavoriteDao favoriteDao) {
            this.favoriteDao = favoriteDao;
        }

        @Override
        protected Void doInBackground(FavoriteNote... favoriteNotes) {
            favoriteDao.insert(favoriteNotes[0]);
            return null;
        }
    }


    private static class UpdateFavoriteAsyncTask extends AsyncTask<FavoriteNote, Void, Void>{
        private FavoriteDao favoriteDao;

        public UpdateFavoriteAsyncTask(FavoriteDao favoriteDao) {
            this.favoriteDao = favoriteDao;
        }

        @Override
        protected Void doInBackground(FavoriteNote... favoriteNotes) {
            favoriteDao.update(favoriteNotes[0]);
            return null;
        }
    }

    private static class DeleteFavoriteAsyncTask extends AsyncTask<FavoriteNote, Void, Void>{
        private FavoriteDao favoriteDao;

        public DeleteFavoriteAsyncTask(FavoriteDao favoriteDao) {
            this.favoriteDao = favoriteDao;
        }

        @Override
        protected Void doInBackground(FavoriteNote... favoriteNotes) {
            favoriteDao.delete(favoriteNotes[0]);
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
