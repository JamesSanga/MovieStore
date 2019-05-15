package com.tz.sanga.moviestore.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.tz.sanga.moviestore.Model.FavoriteDao;
import com.tz.sanga.moviestore.Model.FavoriteDatabase;
import com.tz.sanga.moviestore.Model.FavoriteNote;

import java.util.List;

public class FavoriteRepository {
    private FavoriteDao favoriteDao;
    private LiveData<List<FavoriteNote>>allFavorites;
    private LiveData<List<FavoriteNote>>allFiltered;

    public FavoriteRepository(Application application, String favoriteNote){
        FavoriteDatabase favoriteDatabase = FavoriteDatabase.getInstance(application);
        favoriteDao = favoriteDatabase.favoriteDao();
        allFavorites = favoriteDao.getAllFavorites();
        allFiltered = favoriteDao.checkFavorite(favoriteNote);
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

    public LiveData<List<FavoriteNote>>getAllFavorites(){
        return allFavorites;
    }

    public LiveData<List<FavoriteNote>>checkFavorite(){
        return allFiltered;
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
