package com.printart.nx.popularmovies;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.printart.nx.popularmovies.data.DbDataCall;
import com.printart.nx.popularmovies.databinding.FragmentDetailBinding;
import com.printart.nx.popularmovies.model.MainDataBind;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


public class DetailFragment extends Fragment {

    private static final String TAG = "DetailFragment";
    private FragmentDetailBinding mFragmentDetailBinding;
    private long mMovieId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieId = getActivity().getIntent().getLongExtra("movieID", 0);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mFragmentDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        DbDataCall.requestDetailData(mMovieId)
                .subscribe(new Consumer<MainDataBind>() {
                    @Override
                    public void accept(@NonNull MainDataBind mainDataBind) throws Exception {
                        setView(mainDataBind);
                    }
                });
        return mFragmentDetailBinding.getRoot();
    }


    public void setView(MainDataBind detailBinding) {
        mFragmentDetailBinding.setMovieDetail(detailBinding);
    }
}
