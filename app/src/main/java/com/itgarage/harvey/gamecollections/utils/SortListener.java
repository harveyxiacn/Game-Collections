package com.itgarage.harvey.gamecollections.utils;

/**
 * This interface contains sorting functions for tabs.
 */
public interface SortListener {
    /**
     * Sort list by title.
     */
    public void onSortByTitle();
    /**
     * Sort list by platform.
     */
    public void onSortByPlatform();
    /**
     * Sort list by rating.
     */
    public void onSortByRating();
}
