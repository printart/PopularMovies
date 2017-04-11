package com.printart.nx.popularmovies.model;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.printart.nx.popularmovies.DetailActivity;

public class MainDataBind {

    private static final String TAG = "MainDataBind";
    private String mMovieTitle;
    private String mMovieOverview;
    private String mMovieReleaseDate;
    private double mMovieVoteAverage;
    private long mMovieId;
    private String mMoviePosterUrl;
    private String mMovieRevenue;
    private String mMovieRuntime;
    private String mMovieVideoLink1;
    private String mMovieVideoLink2;

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
                        double movieVoteAverage, String moviePosterUrl, String revenue,
                        String runtime, String videoLink1, String videoLink2) {
        mMovieTitle = movieTitle;
        mMovieOverview = movieOverview;
        mMovieReleaseDate = movieReleaseDate;
        mMovieVoteAverage = movieVoteAverage;
        mMoviePosterUrl = moviePosterUrl;
        mMovieRevenue = revenue;
        mMovieRuntime = runtime;
        mMovieVideoLink1 = videoLink1;
        mMovieVideoLink2 = videoLink2;
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

    public String getMovieRevenue() {
        return mMovieRevenue;
    }

    public String getMovieRuntime() {
        return mMovieRuntime;
    }

    public String getMovieVideoLink1() {
        return mMovieVideoLink1;
    }

    public String getMovieVideoLink2() {
        return mMovieVideoLink2;
    }

    public void doOnClickItemAction(View view, long movieId) {
        Context context = view.getContext();
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("movieID", movieId);
        context.startActivity(intent);
    }

    public void doOnLinkClick(View view, String url) {
        Log.i(TAG, "doOnLinkClick: url to open:" + url);
    }
}
