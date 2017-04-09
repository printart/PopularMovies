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
    private String mRevenue;
    private String mRuntime;
    private String mVideoLink1;
    private String mVideoLink2;


    public MainDataBind(String movieTitle, String movieOverview, String movieReleaseDate,
                        double movieVoteAverage, long movieId, String moviePosterUrl) {
        mMovieTitle = movieTitle;
        mMovieOverview = movieOverview;
        mMovieReleaseDate = movieReleaseDate;
        mMovieVoteAverage = movieVoteAverage;
        mMovieId = movieId;
        mMoviePosterUrl = moviePosterUrl;
    }

    public MainDataBind(String movieTitle, String movieOverview, String movieReleaseDate,
                        double movieVoteAverage, long movieId, String moviePosterUrl,
                        String revenue, String runtime, String videoLink1, String videoLink2) {
//        mMovieTitle = movieTitle;
//        mMovieOverview = movieOverview;
//        mMovieReleaseDate = movieReleaseDate;
//        mMovieVoteAverage = movieVoteAverage;
//        mMovieId = movieId;
//        mMoviePosterUrl = moviePosterUrl;
        this(movieTitle,movieOverview,movieReleaseDate,movieVoteAverage,movieId,moviePosterUrl);//performance hit
        mRevenue = revenue;
        mRuntime = runtime;
        mVideoLink1 = videoLink1;
        mVideoLink2 = videoLink2;
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

    public String getRevenue() {
        return mRevenue;
    }

    public String getRuntime() {
        return mRuntime;
    }

    public String getVideoLink1() {
        return mVideoLink1;
    }

    public String getVideoLink2() {
        return mVideoLink2;
    }

    public void doOnClickItemAction(View view, long movieId) {
        Log.i(TAG, "Clicked:" + movieId);
    }
}
