package com.printart.nx.popularmovies;

import android.app.Fragment;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.printart.nx.popularmovies.adapter.MainAdapter;
import com.printart.nx.popularmovies.databinding.FragmentRecyclerViewBinding;
import com.printart.nx.popularmovies.model.MainDataBind;
import com.printart.nx.popularmovies.network.NetworkCall;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class PopularFragment extends Fragment {

    private static final String TAG = "NowPlayingFragment";
    private RecyclerView mRecyclerView;
    private Disposable mDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startDataFetch();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        FragmentRecyclerViewBinding nowPlayingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recycler_view, container, false);
        mRecyclerView = nowPlayingBinding.appBasicRecyclerView;
        return nowPlayingBinding.getRoot();
    }

    private void startDataFetch() {
        final String category = "popular";

        NetworkCall.networkCallStart(category,0)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<List<MainDataBind>>() {
                    @Override
                    public void accept(@NonNull List<MainDataBind> mainDataBindList) throws Exception {
                        setAdapter(mainDataBindList);
                    }
                })
                .subscribe(new Consumer<List<MainDataBind>>() {
                    @Override
                    public void accept(@NonNull List<MainDataBind> mainDataBindList) throws Exception {
                        NetworkCall.updateDbSecondRequest(mainDataBindList, category);
                    }
                });


    }

    private void setAdapter(List<MainDataBind> list) {
        MainAdapter adapter = new MainAdapter(list);
        GridLayoutManager gridLayoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        }
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(adapter);
    }
}
