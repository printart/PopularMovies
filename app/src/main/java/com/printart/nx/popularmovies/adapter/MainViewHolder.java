package com.printart.nx.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;

import com.printart.nx.popularmovies.databinding.ItemLayoutBinding;
import com.printart.nx.popularmovies.model.MainDataBind;

public class MainViewHolder extends RecyclerView.ViewHolder {
    private ItemLayoutBinding mItemLayoutBinding;

    public MainViewHolder(ItemLayoutBinding itemLayoutBinding) {
        super(itemLayoutBinding.getRoot());
        mItemLayoutBinding = itemLayoutBinding;
    }

    public void setBindingView(MainDataBind mainDataBind) {
        mItemLayoutBinding.setMovieData(mainDataBind);
    }
}
