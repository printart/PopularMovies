package com.printart.nx.popularmovies.network;


import android.databinding.BindingAdapter;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.printart.nx.popularmovies.R;
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
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class NetworkCall {

    private static final String TAG = "networkCall";
    private static final String API_KEY_QUERY_PARAMETER = "api_key";
    private static String mApiKey;
    private static NetworkCall sNetworkCall;

    public static NetworkCall newInstance(String apiKey) {
        mApiKey = apiKey;
        if (sNetworkCall == null) {
            sNetworkCall = new NetworkCall();
        }
//        sMainDataBindList = new ArrayList<>();
//        sVideoAdditionalData = new ArrayList<>();
        return sNetworkCall;
    }

    public static Single<List<MainDataBind>> networkFirstCall(String category) {
        return Single.just(buildFirstRequestUrl(category)).subscribeOn(Schedulers.io()).flatMap(new Function<String, SingleSource<List<MainDataBind>>>() {
            @Override
            public SingleSource<List<MainDataBind>> apply(@NonNull String url) throws Exception {
                String result = networkCall(url);
//                Log.e(TAG, "SingleSource: called:>"+result);
                return Single.just(parseFirstRequestJson(result));
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
        Log.i(TAG, "networkCall: called");
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
    private static List<MainDataBind> parseFirstRequestJson(String json) {
        List<MainDataBind> mainDataBinds = new ArrayList<>();
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
                mainDataBinds.add(new MainDataBind(movieTitle, movieOverview, movieReleaseDate, movieVoteAverage, movieId, moviePosterUrl));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mainDataBinds;
    }

    // set image
    @BindingAdapter({"bind:loadImage"})
    public static void setPoster(ImageView imageView, String url) {
        Picasso.with(imageView.getContext())
                .load(url)
                .error(R.drawable.no_image)
                .into(imageView);
    }
}
