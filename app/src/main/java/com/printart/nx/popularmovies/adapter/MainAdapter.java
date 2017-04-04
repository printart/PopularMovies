package com.printart.nx.popularmovies.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printart.nx.popularmovies.R;
import com.printart.nx.popularmovies.databinding.ItemLayoutBinding;
import com.printart.nx.popularmovies.model.MainDataBind;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {

    private List<MainDataBind> mDataBindList;

    public MainAdapter(List<MainDataBind> list) {
        mDataBindList = list;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        ItemLayoutBinding itemViewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_layout, parent, false);
        return new MainViewHolder(itemViewBinding);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        holder.setBindingView(mDataBindList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataBindList.size();
    }
}
