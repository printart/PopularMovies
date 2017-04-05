package com.printart.nx.popularmovies.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.printart.nx.popularmovies.model.MainDataBind;
import com.printart.nx.popularmovies.network.NetworkCall;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DbData {

    private static DbData sDataTest;
    private static final String TAG = "DbData";
    private static SQLiteDatabase sDb;
    private static long sMovieId;

    public static DbData newInstance(Context context) {
        if (sDataTest == null) {
            sDataTest = new DbData();
        }
        sDb = DbHelper.newInstance(context.getApplicationContext()).getWritableDatabase();
        return sDataTest;
    }

    private DbData() {

    }

    public static long insertDataInDb(List<MainDataBind> firstDataList) {
        final ContentValues contentValues = new ContentValues();
        Observable.fromIterable(firstDataList)
                .subscribeOn(Schedulers.io())
                .map(new Function<MainDataBind, Long>() {
                    @Override
                    public Long apply(@NonNull MainDataBind mainDataBind) throws Exception {
                        contentValues.put(DbContract.NowPlaying.COLUMN_TITLE, mainDataBind.getMovieTitle());
                        contentValues.put(DbContract.NowPlaying.COLUMN_MOVIE_OVERVIEW, mainDataBind.getMovieOverview());
                        contentValues.put(DbContract.NowPlaying.COLUMN_RELEASE_DATE, mainDataBind.getMovieReleaseDate());
                        contentValues.put(DbContract.NowPlaying.COLUMN_VOTE_AVERAGE, mainDataBind.getMovieVoteAverage());
                        contentValues.put(DbContract.NowPlaying.COLUMN_MOVIE_ID, mainDataBind.getMovieId());
                        contentValues.put(DbContract.NowPlaying.COLUMN_POSTER_URL, mainDataBind.getMoviePosterUrl());
                        sDb.insert(DbContract.NowPlaying.TABLE, null, contentValues);
                        contentValues.clear();
                        sMovieId = mainDataBind.getMovieId();
                        return sMovieId;
                    }
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long mId) {
                        NetworkCall.networkSecondCall(mId);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

//call next Category
        return sMovieId;
    }

    public static long updateDataInDb(ContentValues contentValues, Long movieId) {
        String where = DbContract.NowPlaying.COLUMN_MOVIE_ID + "=?";
        String[] args = {String.valueOf(movieId)};
        return sDb.update(DbContract.NowPlaying.TABLE, contentValues, where, args);
    }
}

