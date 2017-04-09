package com.printart.nx.popularmovies.network;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.printart.nx.popularmovies.R;
import com.printart.nx.popularmovies.data.DbContract;
import com.printart.nx.popularmovies.data.DbHelper;
import com.printart.nx.popularmovies.model.MainDataBind;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class NetworkCall {

    private static final String TAG = "networkCall";
    private static final String API_KEY_QUERY_PARAMETER = "api_key";
    private static final String VIDEO_QUERY_PARAMETER = "v";
    private static final String APPEND_TO_RESPONSE_PARAMETER = "append_to_response";
    private static String mApiKey;
    private static NetworkCall sNetworkCall;
    private static SQLiteDatabase sDb;
    private static long mMovieId;

    public static NetworkCall newInstance(Context context, String apiKey) {
        if (sNetworkCall == null) {
            sNetworkCall = new NetworkCall();
        }
        mApiKey = apiKey;
        sDb = DbHelper.newInstance(context.getApplicationContext()).getWritableDatabase();
        return sNetworkCall;
    }

    private NetworkCall() {
    }

    public static Observable<List<MainDataBind>> networkCallStart(String category) {
        switch (category) {
            case "now_playing":
                return networkCallStart(category, 0);
            case "popular":
                return networkCallStart(category, 20000);
            case "top_rated":
                return networkCallStart(category, 10000);
            default:
                return null;
        }
    }

    //pull data with delay
    public static Observable<List<MainDataBind>> networkCallStart(String category, int delay) {
        return Observable.just(category)
//                .delay(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String buildUrl) throws Exception {
                        return createFirstUrl(buildUrl);
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String url) throws Exception {
                        return networkCall(url);
                    }
                })
                .map(new Function<String, List<MainDataBind>>() {
                    @Override
                    public List<MainDataBind> apply(@NonNull String json) throws Exception {
                        return parseJsonFirstRequest(json);
                    }
                });
    }

    //first data pull url
    private static String createFirstUrl(String category) {
        category = TextUtils.isEmpty(category) ? "now_playing" : category;
        String baseUrl = "https://api.themoviedb.org/3/movie/";
        Uri uri = Uri.parse(baseUrl).buildUpon().appendPath(category).appendQueryParameter(API_KEY_QUERY_PARAMETER, mApiKey).build();
        return uri.toString();
    }

    //all network call
    private static String networkCall(String address) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpsURLConnection httpsURLConnection = null;
        try {
            URL url = new URL(address);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            if (httpsURLConnection.getResponseCode() != 200) {
                Log.i(TAG, "networkCall: is:" + httpsURLConnection.getResponseCode());
            }
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        }
        return stringBuilder.toString();//JSON result
    }

    //result for first request in List
    private static List<MainDataBind> parseJsonFirstRequest(String json) {
        List<MainDataBind> sMainDataBinds = new ArrayList<>();
        String posterBaseUrl = "http://image.tmdb.org/t/p/w185";
        try {
            JSONObject job1 = new JSONObject(json);
            JSONArray jar1 = job1.getJSONArray("results");
            for (int i = 0; i < jar1.length(); i++) {
                JSONObject job2 = jar1.getJSONObject(i);
                long movieId = job2.getLong("id");
                String movieTitle = job2.getString("original_title");
                String movieOverview = job2.getString("overview");
                String movieReleaseDate = job2.getString("release_date");
                double movieVoteAverage = job2.getDouble("vote_average");
                String moviePosterUrl = posterBaseUrl + job2.getString("poster_path");
                sMainDataBinds.add(new MainDataBind(movieTitle, movieOverview, movieReleaseDate, movieVoteAverage, movieId, moviePosterUrl));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sMainDataBinds;
    }

    //update DB with additional data List in
    public static void updateDbSecondRequest(List<MainDataBind> mainDataBind, final String category) {
        Observable.fromIterable(mainDataBind)
                .subscribeOn(Schedulers.io())
                .map(new Function<MainDataBind, Long>() {
                    @Override
                    public Long apply(@NonNull MainDataBind mainDataBind) throws Exception {
                        switch (category) {
                            case "now_playing":
                                return updateNowPlaying(mainDataBind);
                            case "popular":
                                return updatePopular(mainDataBind);
                            case "top_rated":
                                return updateTopRated(mainDataBind);
                            default:
                                return null;
                        }
                    }
                })
                .map(new Function<Long, String>() {
                    @Override
                    public String apply(@NonNull Long mId) throws Exception {
                        return buildSecondRequestUrl(mId);
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String url) throws Exception {
                        //network call from second request
                        return networkCall(url);
                    }
                })
                .map(new Function<String, ContentValues>() {
                    @Override
                    public ContentValues apply(@NonNull String json) throws Exception {
                        return parseJsonSecondRequest(json, category);
                    }
                })
                .map(new Function<ContentValues, Long>() {
                    @Override
                    public Long apply(@NonNull ContentValues contentValues) throws Exception {
                        return updateDataInDb(contentValues, category);
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        Log.i(TAG, "accept: movie inserted:" + aLong);
                    }
                });
    }

    private static String buildSecondRequestUrl(long movieId) {
//        https://api.themoviedb.org/3/movie/321612/videos?api_key
        String baseUrl = "https://api.themoviedb.org/3/movie/";
        return Uri.parse(baseUrl).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendQueryParameter(API_KEY_QUERY_PARAMETER, mApiKey)
                .appendQueryParameter(APPEND_TO_RESPONSE_PARAMETER, "videos")
                .build().toString();
    }

    private static Long updateNowPlaying(@NonNull MainDataBind mainDataBind) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.NowPlaying.COLUMN_TITLE, mainDataBind.getMovieTitle());
        contentValues.put(DbContract.NowPlaying.COLUMN_MOVIE_OVERVIEW, mainDataBind.getMovieOverview());
        contentValues.put(DbContract.NowPlaying.COLUMN_RELEASE_DATE, mainDataBind.getMovieReleaseDate());
        contentValues.put(DbContract.NowPlaying.COLUMN_VOTE_AVERAGE, mainDataBind.getMovieVoteAverage());
        contentValues.put(DbContract.NowPlaying.COLUMN_MOVIE_ID, mainDataBind.getMovieId());
        contentValues.put(DbContract.NowPlaying.COLUMN_POSTER_URL, mainDataBind.getMoviePosterUrl());
        sDb.insert(DbContract.NowPlaying.TABLE, null, contentValues);
        mMovieId = mainDataBind.getMovieId();
        return mMovieId;
    }

    private static Long updatePopular(@NonNull MainDataBind mainDataBind) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.Popular.COLUMN_TITLE, mainDataBind.getMovieTitle());
        contentValues.put(DbContract.Popular.COLUMN_MOVIE_OVERVIEW, mainDataBind.getMovieOverview());
        contentValues.put(DbContract.Popular.COLUMN_RELEASE_DATE, mainDataBind.getMovieReleaseDate());
        contentValues.put(DbContract.Popular.COLUMN_VOTE_AVERAGE, mainDataBind.getMovieVoteAverage());
        contentValues.put(DbContract.Popular.COLUMN_MOVIE_ID, mainDataBind.getMovieId());
        contentValues.put(DbContract.Popular.COLUMN_POSTER_URL, mainDataBind.getMoviePosterUrl());
        sDb.insert(DbContract.Popular.TABLE, null, contentValues);
        mMovieId = mainDataBind.getMovieId();
        return mMovieId;
    }

    private static Long updateTopRated(@NonNull MainDataBind mainDataBind) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.TopRated.COLUMN_TITLE, mainDataBind.getMovieTitle());
        contentValues.put(DbContract.TopRated.COLUMN_MOVIE_OVERVIEW, mainDataBind.getMovieOverview());
        contentValues.put(DbContract.TopRated.COLUMN_RELEASE_DATE, mainDataBind.getMovieReleaseDate());
        contentValues.put(DbContract.TopRated.COLUMN_VOTE_AVERAGE, mainDataBind.getMovieVoteAverage());
        contentValues.put(DbContract.TopRated.COLUMN_MOVIE_ID, mainDataBind.getMovieId());
        contentValues.put(DbContract.TopRated.COLUMN_POSTER_URL, mainDataBind.getMoviePosterUrl());
        sDb.insert(DbContract.TopRated.TABLE, null, contentValues);
        mMovieId = mainDataBind.getMovieId();
        return mMovieId;
    }


    //second data data
    private static ContentValues parseJsonSecondRequest(String json, String category) {
        ContentValues contentValues = new ContentValues();
        try {
            JSONObject job1 = new JSONObject(json);
            String revenue = String.valueOf(job1.getInt("revenue") != 0 ? job1.getInt("revenue") : "0");
            String runTime = String.valueOf(job1.getInt("runtime") != 0 ? job1.getInt("runtime") : "0");
            JSONObject job2 = job1.getJSONObject("videos");
            JSONArray jar1 = job2.getJSONArray("results");
            switch (category) {
                case "now_playing":
                    contentValues.put(DbContract.NowPlaying.COLUMN_REVENUE, revenue);
                    contentValues.put(DbContract.NowPlaying.COLUMN_RUNTIME, runTime);
                    if (jar1.length() == 0) {
                        contentValues.put(DbContract.NowPlaying.COLUMN_TRAILER_LINK1, "emp");
                        contentValues.put(DbContract.NowPlaying.COLUMN_TRAILER_LINK2, "emp");
                    } else {
                        if (jar1.length() == 1) {
                            contentValues.put(DbContract.NowPlaying.COLUMN_TRAILER_LINK1, buildSecondRequestYoutubeUrl(jar1.getJSONObject(0).getString("key")));
                        } else {
                            contentValues.put(DbContract.NowPlaying.COLUMN_TRAILER_LINK1, buildSecondRequestYoutubeUrl(jar1.getJSONObject(0).getString("key")));
                            contentValues.put(DbContract.NowPlaying.COLUMN_TRAILER_LINK2, buildSecondRequestYoutubeUrl(jar1.getJSONObject(1).getString("key")));
                        }
                    }
                    break;
                case "popular":
                    contentValues.put(DbContract.Popular.COLUMN_REVENUE, revenue);
                    contentValues.put(DbContract.Popular.COLUMN_RUNTIME, runTime);
                    if (jar1.length() == 0) {
                        contentValues.put(DbContract.Popular.COLUMN_TRAILER_LINK1, "emp");
                        contentValues.put(DbContract.Popular.COLUMN_TRAILER_LINK2, "emp");
                    } else {
                        if (jar1.length() == 1) {
                            contentValues.put(DbContract.Popular.COLUMN_TRAILER_LINK1, buildSecondRequestYoutubeUrl(jar1.getJSONObject(0).getString("key")));
                        } else {
                            contentValues.put(DbContract.Popular.COLUMN_TRAILER_LINK1, buildSecondRequestYoutubeUrl(jar1.getJSONObject(0).getString("key")));
                            contentValues.put(DbContract.Popular.COLUMN_TRAILER_LINK2, buildSecondRequestYoutubeUrl(jar1.getJSONObject(1).getString("key")));
                        }
                    }
                    break;
                case "top_rated":
                    contentValues.put(DbContract.TopRated.COLUMN_REVENUE, revenue);
                    contentValues.put(DbContract.TopRated.COLUMN_RUNTIME, runTime);
                    if (jar1.length() == 0) {
                        contentValues.put(DbContract.TopRated.COLUMN_TRAILER_LINK1, "emp");
                        contentValues.put(DbContract.TopRated.COLUMN_TRAILER_LINK2, "emp");
                    } else {
                        if (jar1.length() == 1) {
                            contentValues.put(DbContract.TopRated.COLUMN_TRAILER_LINK1, buildSecondRequestYoutubeUrl(jar1.getJSONObject(0).getString("key")));
                        } else {
                            contentValues.put(DbContract.TopRated.COLUMN_TRAILER_LINK1, buildSecondRequestYoutubeUrl(jar1.getJSONObject(0).getString("key")));
                            contentValues.put(DbContract.TopRated.COLUMN_TRAILER_LINK2, buildSecondRequestYoutubeUrl(jar1.getJSONObject(1).getString("key")));
                        }
                    }
                    break;
                default:
                    return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentValues;
    }

    private static String buildSecondRequestYoutubeUrl(String movieIdKey) {
//        https://www.youtube.com/watch/?v="
        String baseUrl = "https://www.youtube.com";
        return Uri.parse(baseUrl).buildUpon()
                .appendPath("watch")
                .appendQueryParameter(VIDEO_QUERY_PARAMETER, movieIdKey).build().toString();
    }

    public static long updateDataInDb(ContentValues contentValues, String category) {
        String where;
        String[] args = {String.valueOf(mMovieId)};
        switch (category) {
            case "now_playing":
                where = DbContract.NowPlaying.COLUMN_MOVIE_ID + "=?";
                return sDb.update(DbContract.NowPlaying.TABLE, contentValues, where, args);
            case "popular":
                where = DbContract.Popular.COLUMN_MOVIE_ID + "=?";
                return sDb.update(DbContract.Popular.TABLE, contentValues, where, args);
            case "top_rated":
                where = DbContract.TopRated.COLUMN_MOVIE_ID + "=?";
                return sDb.update(DbContract.TopRated.TABLE, contentValues, where, args);
            default:
                return 0;
        }

    }

    private Observable<List<MainDataBind>> dbListQuery(String category) {
        return Observable.just(category)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, Cursor>() {
                    @Override
                    public Cursor apply(@NonNull String table) throws Exception {
                        return sDb.query(table, null, null, null, null, null, null);
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
                            String link1 = cursor.getString(7);
                            String link2 = cursor.getString(8);
                            String runtime = cursor.getString(9);
                            String revenue = cursor.getString(10);
                            mainDataBinds.add(new MainDataBind(title, overview, release, average, movieId, poster, revenue, runtime, link1, link2));
                        }
                        cursor.close();
                        return mainDataBinds;
                    }
                });

    }

    // set image data binding
    @BindingAdapter({"bind:loadImage"})
    public static void getMoviePosterUrl(ImageView imageView, String url) {
        Picasso.with(imageView.getContext())
                .load(url)
                .error(R.drawable.no_image)
                .into(imageView);
    }
}
