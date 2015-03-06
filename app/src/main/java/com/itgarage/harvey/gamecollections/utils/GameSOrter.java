package com.itgarage.harvey.gamecollections.utils;

import com.itgarage.harvey.gamecollections.models.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by harvey on 2015-03-06.
 */
public class GameSorter {

    public void sortGamesByTitle(ArrayList<Game> games){
        Collections.sort(games, new Comparator<Game>() {
            @Override
            public int compare(Game lhs, Game rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
    }

    public void sortGamesByPlatform(ArrayList<Game> games){
        Collections.sort(games, new Comparator<Game>() {
            @Override
            public int compare(Game lhs, Game rhs) {
                return lhs.getPlatform().compareTo(rhs.getPlatform());
            }
        });
    }

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
