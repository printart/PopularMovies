package com.printart.nx.popularmovies.network;


import android.content.ContentValues;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.printart.nx.popularmovies.R;
import com.printart.nx.popularmovies.data.DbContract;
import com.printart.nx.popularmovies.data.DbData;
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

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class NetworkCall {

    private static final String TAG = "networkCall";
    private static final String API_KEY_QUERY_PARAMETER = "api_key";
    private static final String VIDEO_QUERY_PARAMETER = "v";
    public static final String APPEND_TO_RESPONSE_PARAMETER = "append_to_response";
    private static String mApiKey;
    private static NetworkCall sNetworkCall;
    private static ContentValues sContentValues;

    public static NetworkCall newInstance(String apiKey) {
        mApiKey = apiKey;
        if (sNetworkCall == null) {
            sNetworkCall = new NetworkCall();
        }
        return sNetworkCall;
    }

    private NetworkCall() {
    }

    public static Single<List<MainDataBind>> networkFirstCall(String category) {
        return Single.just(category)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String createUrl) throws Exception {
                        return buildFirstRequestUrl(createUrl);
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

    private static String buildFirstRequestUrl(String category) {
        category = TextUtils.isEmpty(category) ? "now_playing" : category;
        String baseUrl = "https://api.themoviedb.org/3/movie/";
        Uri uri = Uri.parse(baseUrl).buildUpon().appendPath(category).appendQueryParameter(API_KEY_QUERY_PARAMETER, mApiKey).build();
        return uri.toString();
    }

    private static String networkCall(String address) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpsURLConnection httpsURLConnection = null;
        try {
            URL url = new URL(address);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
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

    //result for first request
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

    public static void networkSecondCall(final Long movieId) {
        Single.just(movieId).map(new Function<Long, String>() {
            @Override
            public String apply(@NonNull Long mId) throws Exception {
                return buildSecondRequestUrl(mId);
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(@NonNull String url) throws Exception {
                return networkCall(url);
            }
        }).map(new Function<String, ContentValues>() {
            @Override
            public ContentValues apply(@NonNull String json) throws Exception {
                return parseJsonSecondRequest(json);
            }
        }).subscribe(new Consumer<ContentValues>() {
            @Override
            public void accept(@NonNull ContentValues contentValues) throws Exception {
                DbData.updateDataInDb(contentValues, movieId);
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

    private static ContentValues parseJsonSecondRequest(String json) {
        ContentValues contentValues = new ContentValues();
        try {
            JSONObject job1 = new JSONObject(json);
            String revenue = String.valueOf(job1.getInt("revenue") != 0 ? job1.getInt("revenue") : "0");
            contentValues.put(DbContract.NowPlaying.COLUMN_REVENUE, revenue);
            String runTime = String.valueOf(job1.getInt("runtime") != 0 ? job1.getInt("runtime") : "0");
            contentValues.put(DbContract.NowPlaying.COLUMN_RUNTIME, runTime);
            JSONObject job2 = job1.getJSONObject("videos");
            JSONArray jar1 = job2.getJSONArray("results");
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



    // set image data binding
    @BindingAdapter({"bind:loadImage"})
    public static void setPoster(ImageView imageView, String url) {
        Picasso.with(imageView.getContext())
                .load(url)
                .error(R.drawable.no_image)
                .into(imageView);
    }
}