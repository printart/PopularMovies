package com.printart.nx.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.printart.nx.popularmovies.data.DbDataCall;
import com.printart.nx.popularmovies.databinding.ActivityLayoutBinding;
import com.printart.nx.popularmovies.network.NetworkCall;


public class MainActivity extends AppCompatActivity {
//private static final String API_KEY=""

    private int mVisibleMenu;
    private ActivityLayoutBinding mActivityLayoutBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        NetworkCall.newInstance(this,API_KEY);
        //TODO  delete
        NetworkCall.newInstance(getString(R.string.apiKey));
        mActivityLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_layout);
        setSupportActionBar(mActivityLayoutBinding.mainToolbar);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.fragment_container, new NowPlayingFragment()).commit();
            mVisibleMenu = R.id.action_now_playing;
            mActivityLayoutBinding.mainToolbar.setTitle("Now Playing");
        } else {
            mVisibleMenu = savedInstanceState.getInt("visibleMenu");
        }
        DbDataCall.newInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(mVisibleMenu).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_now_playing:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new NowPlayingFragment()).commit();
                mActivityLayoutBinding.mainToolbar.setTitle("Now Playing");
                mVisibleMenu = R.id.action_now_playing;
                invalidateOptionsMenu();
                return true;
            case R.id.action_popular:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new PopularFragment()).commit();
                mActivityLayoutBinding.mainToolbar.setTitle("Popular");
                mVisibleMenu = R.id.action_popular;
                invalidateOptionsMenu();
                return true;
            case R.id.action_top_rated:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new TopRatedFragment()).commit();
                mActivityLayoutBinding.mainToolbar.setTitle("Top Rated");
                mVisibleMenu = R.id.action_top_rated;
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("visibleMenu", mVisibleMenu);
    }
}
