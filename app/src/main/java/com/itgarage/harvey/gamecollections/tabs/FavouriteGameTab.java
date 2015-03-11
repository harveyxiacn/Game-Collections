package com.itgarage.harvey.gamecollections.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.adapters.GameListAdapter;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.models.Game;
import com.itgarage.harvey.gamecollections.utils.GameSorter;
import com.itgarage.harvey.gamecollections.utils.SortListener;
import com.itgarage.harvey.gamecollections.utils.UpdateListListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harvey on 2015-03-05.
 */
public class FavouriteGameTab extends Fragment implements SortListener, UpdateListListener{
    RecyclerView gamesCardListView;
    GameListAdapter gamesAdapter;
    RecyclerView.LayoutManager gamesCardListLayoutManager;
    RecyclerView.LayoutManager gamesCardGridLayoutManager;
    LinearLayout noResultLinearLayout;
    boolean isGridLayout;

    SharedPreferences preferences;
    GamesDataSource dataSource;

    SearchView searchView;
    ArrayList<Game> gamesList;
    GameSorter gameSorter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_favourite_games, container, false);
        dataSource = new GamesDataSource(getActivity());
        gameSorter = new GameSorter();
        gamesList = new ArrayList<>();
        getGameList();
        gamesCardListView = (RecyclerView) rootView.findViewById(R.id.gameCardList);
        searchView = (SearchView) rootView.findViewById(R.id.keyWordSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i("all game tab", "onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<Game> results;
                dataSource.open();
                if(s.isEmpty()){
                    results = dataSource.getAllFavouriteGames();
                }else {
                    results = dataSource.searchKeywordFavourite(s);
                }
                dataSource.close();
                gamesAdapter.updateList(results);
                return false;
            }
        });
        preferences = getActivity().getSharedPreferences("layoutPreference", Context.MODE_PRIVATE);
        isGridLayout = preferences.getBoolean("isGameListLayout", false);
        gamesCardListLayoutManager = new LinearLayoutManager(getActivity());
        // 3 is span size, 3 items in a row
        gamesCardGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        if (isGridLayout) {
            gamesCardListView.setLayoutManager(gamesCardGridLayoutManager);
        } else {
            gamesCardListView.setLayoutManager(gamesCardListLayoutManager);
        }
        gamesAdapter = new GameListAdapter(gamesList, getActivity(), isGridLayout);
        noResultLinearLayout = (LinearLayout) rootView.findViewById(R.id.noGameInDataBaseLinearLayout);

        if (gamesAdapter.getItemCount() == 0) {
            changeUIsWhenDataSetChange(false);
        } else {
            changeUIsWhenDataSetChange(true);
        }

        return rootView;
    }

    /**
     * Change the UIs in this fragment visibility by has data or not.
     *
     * @param hasData If the data set is empty or not.
     */
    private void changeUIsWhenDataSetChange(boolean hasData) {

        if (!hasData) {
            gamesCardListView.setVisibility(View.GONE);
            noResultLinearLayout.setVisibility(View.VISIBLE);

        } else {
            gamesCardListView.setAdapter(gamesAdapter);
            gamesCardListView.setVisibility(View.VISIBLE);
            noResultLinearLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Get games list from DB
     */
    private void getGameList() {
        dataSource.open();
        gamesList = dataSource.getAllFavouriteGames();
        dataSource.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("All favourite game tab", "onResume");
        getGameList();
        gamesAdapter.updateList(gamesList);
        if(gamesAdapter.getItemCount()>0){
            changeUIsWhenDataSetChange(true);
        }else {
            changeUIsWhenDataSetChange(false);
        }
    }

    @Override
    public void onSortByTitle() {
        if(gamesList!=null) {
            gameSorter.sortGamesByTitle(gamesList);
            gamesAdapter.updateList(gamesList);
        }
    }

    @Override
    public void onSortByPlatform() {
        if(gamesList!=null) {
            gameSorter.sortGamesByPlatform(gamesList);
            gamesAdapter.updateList(gamesList);
        }
    }

    @Override
    public void onSortByRating() {
        if(gamesList!=null) {
            gameSorter.sortGamesByRating(gamesList);
            gamesAdapter.updateList(gamesList);
        }
    }

    @Override
    public void updateAdapterList(GamesDataSource dataSource) {
        gamesAdapter.updateList(dataSource.getAllFavouriteGames());
        if(gamesAdapter.getItemCount()>0){
            gamesCardListView.setVisibility(View.VISIBLE);
            noResultLinearLayout.setVisibility(View.GONE);
        }else {
            gamesCardListView.setVisibility(View.GONE);
            noResultLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateLayoutManager(RecyclerView.LayoutManager layoutManager, boolean isGridLayout) {
        if(gamesAdapter.getItemCount()>0) {
            gamesAdapter.setGridLayout(isGridLayout);
            gamesCardListView.setLayoutManager(layoutManager);
        }
    }

}
