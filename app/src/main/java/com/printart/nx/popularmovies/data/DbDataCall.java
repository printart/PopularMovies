package com.printart.nx.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.printart.nx.popularmovies.model.MainDataBind;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DbDataCall {

    private static final String TAG = "DbDataCall";
    private static DbDataCall sDbDataCall;
    private static SQLiteDatabase mDb;
    private static String sTable;

    public static DbDataCall newInstance(Context context) {
        if (sDbDataCall == null) {
            sDbDataCall = new DbDataCall(context.getApplicationContext());
        }
        return sDbDataCall;
    }

    private DbDataCall(Context context) {
        mDb = DbHelper.newInstance(context).getWritableDatabase();
    }


    public static long updateDataInDb(ContentValues contentValues, String category, long movieId) {
        String where;
        String[] args = {String.valueOf(movieId)};
        switch (category) {
            case "now_playing":
                sTable = DbContract.NowPlaying.TABLE;
                where = DbContract.NowPlaying.COLUMN_MOVIE_ID + "=?";
                return mDb.update(DbContract.NowPlaying.TABLE, contentValues, where, args);
            case "popular":
                sTable = DbContract.Popular.TABLE;
                where = DbContract.Popular.COLUMN_MOVIE_ID + "=?";
                return mDb.update(DbContract.Popular.TABLE, contentValues, where, args);
            case "top_rated":
                Log.i(TAG, "updateDataInDb: value:"+contentValues.getAsString(DbContract.NowPlaying.COLUMN_REVENUE));
                sTable = DbContract.TopRated.TABLE;
                where = DbContract.TopRated.COLUMN_MOVIE_ID + "=?";
                return mDb.update(DbContract.TopRated.TABLE, contentValues, where, args);
            default:
                return 0;
        }
    }

    public static Long updateNowPlaying(@NonNull MainDataBind mainDataBind) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.NowPlaying.COLUMN_TITLE, mainDataBind.getMovieTitle());
        contentValues.put(DbContract.NowPlaying.COLUMN_MOVIE_OVERVIEW, mainDataBind.getMovieOverview());
        contentValues.put(DbContract.NowPlaying.COLUMN_RELEASE_DATE, mainDataBind.getMovieReleaseDate());
        contentValues.put(DbContract.NowPlaying.COLUMN_VOTE_AVERAGE, mainDataBind.getMovieVoteAverage());
        contentValues.put(DbContract.NowPlaying.COLUMN_MOVIE_ID, mainDataBind.getMovieId());
        contentValues.put(DbContract.NowPlaying.COLUMN_POSTER_URL, mainDataBind.getMoviePosterUrl());
        mDb.insert(DbContract.NowPlaying.TABLE, null, contentValues);
        return mainDataBind.getMovieId();
    }

    public static Long updatePopular(@NonNull MainDataBind mainDataBind) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.Popular.COLUMN_TITLE, mainDataBind.getMovieTitle());
        contentValues.put(DbContract.Popular.COLUMN_MOVIE_OVERVIEW, mainDataBind.getMovieOverview());
        contentValues.put(DbContract.Popular.COLUMN_RELEASE_DATE, mainDataBind.getMovieReleaseDate());
        contentValues.put(DbContract.Popular.COLUMN_VOTE_AVERAGE, mainDataBind.getMovieVoteAverage());
        contentValues.put(DbContract.Popular.COLUMN_MOVIE_ID, mainDataBind.getMovieId());
        contentValues.put(DbContract.Popular.COLUMN_POSTER_URL, mainDataBind.getMoviePosterUrl());
        mDb.insert(DbContract.Popular.TABLE, null, contentValues);
        return mainDataBind.getMovieId();
    }

    public static Long updateTopRated(@NonNull MainDataBind mainDataBind) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.TopRated.COLUMN_TITLE, mainDataBind.getMovieTitle());
        contentValues.put(DbContract.TopRated.COLUMN_MOVIE_OVERVIEW, mainDataBind.getMovieOverview());
        contentValues.put(DbContract.TopRated.COLUMN_RELEASE_DATE, mainDataBind.getMovieReleaseDate());
        contentValues.put(DbContract.TopRated.COLUMN_VOTE_AVERAGE, mainDataBind.getMovieVoteAverage());
        contentValues.put(DbContract.TopRated.COLUMN_MOVIE_ID, mainDataBind.getMovieId());
        contentValues.put(DbContract.TopRated.COLUMN_POSTER_URL, mainDataBind.getMoviePosterUrl());
        mDb.insert(DbContract.TopRated.TABLE, null, contentValues);
        return mainDataBind.getMovieId();
    }

    public static Observable<List<MainDataBind>> dbListQuery(String table) {
        sTable = table;
        return Observable.just(table)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, Cursor>() {
                    @Override
                    public Cursor apply(@NonNull String table) throws Exception {
                        return mDb.query(table, null, null, null, null, null, null);
                    }
                })
                .map(new Function<Cursor, List<MainDataBind>>() {
                    @Override
                    public List<MainDataBind> apply(@NonNull Cursor cursor) throws Exception {
                        List<MainDataBind> mainDataBinds = new ArrayList<>();
                        while (cursor.moveToNext()) {
                            String title = cursor.getString(1);
                            String overview = cursor.getString(2);
                            String release = cursor.getString(3);
                            double average = cursor.getDouble(4);
                            long movieId = cursor.getLong(5);
                            String poster = cursor.getString(6);
//                            String link1 = cursor.getString(7);
//                            String link2 = cursor.getString(8);
//                            String runtime = cursor.getString(9);
//                            String revenue = cursor.getString(10);
                            mainDataBinds.add(new MainDataBind(title, overview, release, average, movieId, poster));
                        }
                        cursor.close();
                        return mainDataBinds;
                    }
                });
    }

    public static Observable<MainDataBind> requestDetailData(long movieId) {
        return Observable.just(movieId)
                .observeOn(Schedulers.io())
                .map(new Function<Long, Cursor>() {
                    @Override
                    public Cursor apply(@NonNull Long mId) throws Exception {
                        return requestCursor(mId);
                    }
                })
                .map(new Function<Cursor, MainDataBind>() {
                    @Override
                    public MainDataBind apply(@NonNull Cursor cursor) throws Exception {
                        MainDataBind mainDataBind = null;
                        while (cursor.moveToNext()) {
                            String title = cursor.getString(1);
                            Log.i(TAG, "apply: title"+title);
                            String overview = cursor.getString(2);
                            String year = cursor.getString(3);
                            double vote = cursor.getDouble(4);
                            String poster = cursor.getString(6);
                            String link1 = cursor.getString(7);
                            String link2 = cursor.getString(8);
                            String runtime = cursor.getString(9);
                            String revenue = cursor.getString(10);
                            mainDataBind = new MainDataBind(title, overview, year, vote, poster, revenue, runtime, link1, link2);
                        }
                        cursor.close();
                        return mainDataBind;
                    }
                });
    }

    private static Cursor requestCursor(Long mId) {
        String[] args = {String.valueOf(mId)};
        String where = null;
        switch (sTable) {
            case DbContract.NowPlaying.TABLE:
                where = DbContract.NowPlaying.COLUMN_MOVIE_ID + "=?";
                break;
            case DbContract.Popular.TABLE:
                where = DbContract.Popular.COLUMN_MOVIE_ID + "=?";
                break;
            case DbContract.TopRated.TABLE:
                where = DbContract.TopRated.COLUMN_MOVIE_ID + "=?";
                break;
            default:
                break;
        }
        return mDb.query(sTable, null, where, args, null, null, null);
    }

    public static boolean checkForLocalData(String category) {
        return DatabaseUtils.queryNumEntries(mDb, category) > 0;
    }
}