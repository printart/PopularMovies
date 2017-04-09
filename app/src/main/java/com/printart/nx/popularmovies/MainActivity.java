package com.printart.nx.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.printart.nx.popularmovies.databinding.ActivityLayoutBinding;
import com.printart.nx.popularmovies.network.NetworkCall;


public class MainActivity extends AppCompatActivity {
//private static final String API_KEY=""

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        NetworkCall.newInstance(API_KEY);
        //TODO  delete
        NetworkCall.newInstance(this, getString(R.string.apiKey));
        ActivityLayoutBinding activityLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_layout);
        setSupportActionBar(activityLayoutBinding.mainToolbar);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.fragment_container, new TopratedFragment()).commit();
//            getFragmentManager().beginTransaction().add(R.id.fragment_container, new NowPlayingFragment()).commit();
        }
    }
}
