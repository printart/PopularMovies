package com.printart.nx.popularmovies.model;


import android.util.Log;
import android.view.View;

import static android.content.ContentValues.TAG;

public class MainDataBind {

    //    private static final String TAG = "FirstData";
    private String mMovieTitle;
    private String mMovieOverview;
    private String mMovieReleaseDate;
    private double mMovieVoteAverage;
    private long mMovieId;
    private String mMoviePosterUrl;

    public MainDataBind(String movieTitle, String movieOverview, String movieReleaseDate, double movieVoteAverage, long movieId, String moviePosterUrl) {
        mMovieTitle = movieTitle;
        mMovieOverview = movieOverview;
        mMovieReleaseDate = movieReleaseDate;
        mMovieVoteAverage = movieVoteAverage;
        mMovieId = movieId;
        mMoviePosterUrl = moviePosterUrl;
    }


    public String getMovieTitle() {
        return mMovieTitle;
    }

    public String getMovieOverview() {
        return mMovieOverview;
    }

    public String getMovieReleaseDate() {
        return mMovieReleaseDate.split("-")[0];
    }

    public String getMovieVoteAverage() {
        return String.valueOf(mMovieVoteAverage);
    }

    public long getMovieId() {
        return mMovieId;
    }

    public String getMoviePosterUrl() {
        return mMoviePosterUrl;
    }

    public void doOnClickItemAction(View view, long movieId) {
        Log.i(TAG, "Clicked:" + movieId);
    }
}
