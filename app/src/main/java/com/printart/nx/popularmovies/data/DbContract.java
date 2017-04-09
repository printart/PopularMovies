package com.printart.nx.popularmovies.data;


import android.provider.BaseColumns;

public final class DbContract {

    public static class NowPlaying {
        public static final String TABLE = "nowPlaying";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_POSTER_URL = "posterUrl";
        public static final String COLUMN_REVENUE = "revenue";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_TRAILER_LINK1 = "trailerLink1";
        public static final String COLUMN_TRAILER_LINK2 = "trailerLink2";
    }

    public static class Popular {
        public static final String TABLE = "popular";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_POSTER_URL = "posterUrl";
        public static final String COLUMN_REVENUE = "revenue";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_TRAILER_LINK1 = "trailerLink1";
        public static final String COLUMN_TRAILER_LINK2 = "trailerLink2";
    }

    public static class TopRated {
        public static final String TABLE = "topRated";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_POSTER_URL = "posterUrl";
        public static final String COLUMN_REVENUE = "revenue";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_TRAILER_LINK1 = "trailerLink1";
        public static final String COLUMN_TRAILER_LINK2 = "trailerLink2";
    }
}
