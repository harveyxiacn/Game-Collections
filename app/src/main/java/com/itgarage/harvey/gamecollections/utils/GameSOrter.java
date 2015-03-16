package com.itgarage.harvey.gamecollections.utils;

import com.itgarage.harvey.gamecollections.models.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class create functions for sorting.
 */
public class GameSorter {
    /**
     * Sort games list by title.
     * @param games Unordered games list.
     */
    public void sortGamesByTitle(ArrayList<Game> games){
        Collections.sort(games, new Comparator<Game>() {
            @Override
            public int compare(Game lhs, Game rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
    }
    /**
     * Sort games list by platform.
     * @param games Unordered games list.
     */
    public void sortGamesByPlatform(ArrayList<Game> games){
        Collections.sort(games, new Comparator<Game>() {
            @Override
            public int compare(Game lhs, Game rhs) {
                return lhs.getPlatform().compareTo(rhs.getPlatform());
            }
        });
    }
    /**
     * Sort games list by rating.
     * @param games Unordered games list.
     */
    public void sortGamesByRating(ArrayList<Game> games){
        Collections.sort(games, new Comparator<Game>() {
            @Override
            public int compare(Game lhs, Game rhs) {
                int ratingLhs = lhs.getRating();
                int ratingRhs = rhs.getRating();
                if(ratingLhs<ratingRhs){
                    return 1;
                }else if(ratingLhs>ratingRhs){
                    return -1;
                }else {
                    return 0;
                }
            }
        });
    }
}
