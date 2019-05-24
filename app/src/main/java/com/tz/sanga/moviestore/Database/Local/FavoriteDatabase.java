package com.tz.sanga.moviestore.Database.Local;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {Favorite.class}, version = 1)
public abstract class FavoriteDatabase extends RoomDatabase {

    private static FavoriteDatabase instance;
    private static RoomDatabase.Callback roomDatabase = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    public static synchronized FavoriteDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), FavoriteDatabase.class, "favorite")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomDatabase)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract FavoriteDao favoriteDao();

}
