package com.itgarage.harvey.gamecollections.utils;

import android.support.v7.widget.RecyclerView;

import com.itgarage.harvey.gamecollections.db.GamesDataSource;

/**
 * Created by harvey on 2015-03-10.
 */
public interface UpdateListListener {
    public void updateAdapterList(GamesDataSource dataSource);
    public void updateLayoutManager(RecyclerView.LayoutManager layoutManager, boolean isGridLayout);
}
