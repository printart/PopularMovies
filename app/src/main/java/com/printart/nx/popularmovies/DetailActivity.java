package com.printart.nx.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.printart.nx.popularmovies.databinding.ActivityLayoutBinding;
import com.printart.nx.popularmovies.model.MainDataBind;


public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    private MainDataBind mMainDataBind;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: DetailActivity called");
        ActivityLayoutBinding activityLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_layout);
        setSupportActionBar(activityLayoutBinding.mainToolbar);

        long movieId = getIntent().getLongExtra("movieID", 0);
       /* DbDataCall.requestDetailData(movieId)
                .subscribe(new Consumer<MainDataBind>() {
                    @Override
                    public void accept(@NonNull MainDataBind mainDataBind) throws Exception {
                        mMainDataBind = mainDataBind;
                    }
                });*/

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.fragment_container, new DetailFragment()).commit();
//            getFragmentManager().beginTransaction().add(R.id.fragment_container, DetailFragment.newInstance(mMainDataBind)).commit();
        }
    }
}
