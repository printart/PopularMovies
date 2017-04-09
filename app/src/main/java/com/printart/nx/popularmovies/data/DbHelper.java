package com.printart.nx.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "movie.db";
    public static final int DB_VERSION = 1;
    private static DbHelper sDbHelper;

    public static DbHelper newInstance(Context context) {
        if (sDbHelper == null) {
            sDbHelper = new DbHelper(context);
        }
        return sDbHelper;
    }

    private DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createSQL(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createSQL(SQLiteDatabase db) {
        String createNowPlayingTable = "create table "
                + DbContract.NowPlaying.TABLE
                + "(" + DbContract.NowPlaying.COLUMN_ID + " integer primary key autoincrement, "//0
                + DbContract.NowPlaying.COLUMN_TITLE + " text not null, "//1
                + DbContract.NowPlaying.COLUMN_MOVIE_OVERVIEW + " text default null, "//2
                + DbContract.NowPlaying.COLUMN_RELEASE_DATE + " text default null, "//3
                + DbContract.NowPlaying.COLUMN_VOTE_AVERAGE + " real default 0.0, "//4
                + DbContract.NowPlaying.COLUMN_MOVIE_ID + " integer not null, "//5
                + DbContract.NowPlaying.COLUMN_POSTER_URL + " text default null, "//6
                + DbContract.NowPlaying.COLUMN_TRAILER_LINK1 + " text default null, "//7
                + DbContract.NowPlaying.COLUMN_TRAILER_LINK2 + " text default null, "//8
                + DbContract.NowPlaying.COLUMN_RUNTIME + " text default null, "//9
                + DbContract.NowPlaying.COLUMN_REVENUE + " text default null"//10
                + ")";
        db.execSQL(createNowPlayingTable);

        String createPopular = "create table "
                + DbContract.Popular.TABLE
                + "(" + DbContract.Popular.COLUMN_ID + " integer primary key autoincrement, "//0
                + DbContract.Popular.COLUMN_TITLE + " text not null, "//1
                + DbContract.Popular.COLUMN_MOVIE_OVERVIEW + " text default null, "//2
                + DbContract.Popular.COLUMN_RELEASE_DATE + " text default null, "//3
                + DbContract.Popular.COLUMN_VOTE_AVERAGE + " real default 0.0, "//4
                + DbContract.Popular.COLUMN_MOVIE_ID + " integer not null, "//5
                + DbContract.Popular.COLUMN_POSTER_URL + " text default null, "//6
                + DbContract.Popular.COLUMN_TRAILER_LINK1 + " text default null, "//7
                + DbContract.Popular.COLUMN_TRAILER_LINK2 + " text default null, "//8
                + DbContract.Popular.COLUMN_RUNTIME + " text default null, "//9
                + DbContract.Popular.COLUMN_REVENUE + " text default null"//10
                + ")";
        db.execSQL(createPopular);

        String createTopRated = "create table "
                + DbContract.TopRated.TABLE
                + "(" + DbContract.TopRated.COLUMN_ID + " integer primary key autoincrement, "//0
                + DbContract.TopRated.COLUMN_TITLE + " text not null, "//1
                + DbContract.TopRated.COLUMN_MOVIE_OVERVIEW + " text default null, "//2
                + DbContract.TopRated.COLUMN_RELEASE_DATE + " text default null, "//3
                + DbContract.TopRated.COLUMN_VOTE_AVERAGE + " real default 0.0, "//4
                + DbContract.TopRated.COLUMN_MOVIE_ID + " integer not null, "//5
                + DbContract.TopRated.COLUMN_POSTER_URL + " text default null, "//6
                + DbContract.TopRated.COLUMN_TRAILER_LINK1 + " text default null, "//7
                + DbContract.TopRated.COLUMN_TRAILER_LINK2 + " text default null, "//8
                + DbContract.TopRated.COLUMN_RUNTIME + " text default null, "//9
                + DbContract.TopRated.COLUMN_REVENUE + " text default null"//10
                + ")";
        db.execSQL(createTopRated);
    }
}
