package com.itgarage.harvey.gamecollections.utils;

import android.support.v7.widget.RecyclerView;

/**
 * This interface contains update list functions for tabs.
 */
public interface UpdateListListener {
    /**
     * Update adapter's layout manager.
     * @param layoutManager Use for change layout manager.
     * @param isGridLayout User for indicate the layout is grid or list.
     */
    public void updateLayoutManager(RecyclerView.LayoutManager layoutManager, boolean isGridLayout);
}
