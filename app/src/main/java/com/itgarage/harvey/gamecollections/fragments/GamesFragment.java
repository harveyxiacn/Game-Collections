package com.itgarage.harvey.gamecollections.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
import com.itgarage.harvey.gamecollections.adapters.GameListAdapter;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.models.Game;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends Fragment implements View.OnClickListener{
    static RecyclerView gamesCardListView;
    public static GameListAdapter gamesAdapter;
    RecyclerView.LayoutManager gamesCardListLayoutManager;
    RecyclerView.LayoutManager gamesCardGridLayoutManager;
    public static NaviDrawerActivity naviDrawerActivity;
    static LinearLayout noResultLinearLayout;
    boolean isGridLayout;

    SubActionButton layoutChangeButton, sortByTitleButton, sortByPlatformButton, sortByRatingButton, sortByFavouriteButton;
    ImageView itemLayoutChangeIcon, itemSortByTitletIcon, itemSortByPlatformIcon, itemSortByRatingIcon, itemSortByFavourtiteIcon;
    SubActionButton.Builder itemBuilder;
    FloatingActionMenu gameListActionMenu;
    FloatingActionButton gameListActionButton;
    static final String CHANGE_LAYOUT = "change layout";
    static final String SORT_TITLE = "sort title";
    static final String SORT_PLATFORM = "sort platform";
    static final String SORT_RATING = "sort rating";
    static final String SORT_FAVOURITE = "sort favourite";

    SharedPreferences preferences;
    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static GamesFragment newInstance() {
        GamesFragment fragment = new GamesFragment();
        return fragment;
    }

    public GamesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_games, container,
                false);
        preferences = naviDrawerActivity.getSharedPreferences("layoutPreference", Context.MODE_PRIVATE);
        isGridLayout = preferences.getBoolean("isGameListLayout", false);

        gamesCardListView = (RecyclerView) rootView.findViewById(R.id.gameCardList);
        gamesCardListLayoutManager = new LinearLayoutManager(naviDrawerActivity.getContext());
        // 3 is span size, 3 items in a row
        gamesCardGridLayoutManager = new GridLayoutManager(naviDrawerActivity.getContext(), 3);
        if(isGridLayout) {
            gamesCardListView.setLayoutManager(gamesCardGridLayoutManager);
        }else {
            gamesCardListView.setLayoutManager(gamesCardListLayoutManager);
        }
        gamesAdapter = new GameListAdapter(getGameList(), naviDrawerActivity, isGridLayout);
        noResultLinearLayout = (LinearLayout) rootView.findViewById(R.id.noGameInDataBaseLinearLayout);

        if(gamesAdapter.getItemCount()==0){
            changeUIsWhenDataSetChange(false);
        }else {
            changeUIsWhenDataSetChange(true);
        }
        return rootView;
    }

    /**
     * Change the UIs in this fragment visibility by has data or not.
     * @param hasData If the data set is empty or not.
     */
    public static void changeUIsWhenDataSetChange(boolean hasData){

        if(!hasData){
            gamesCardListView.setVisibility(View.GONE);
            noResultLinearLayout.setVisibility(View.VISIBLE);

        }else {
            gamesCardListView.setAdapter(gamesAdapter);
            gamesCardListView.setVisibility(View.VISIBLE);
            noResultLinearLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NaviDrawerActivity) activity).onSectionAttached(2);
        this.naviDrawerActivity = (NaviDrawerActivity) activity;
    }

    /**
     * Get games list from DB
     * @return A game list if success, null if failed.
     */
    private List<Game> getGameList() {
        List<Game> gamesList = new ArrayList<Game>();
        GamesDataSource dataSource = naviDrawerActivity.getDataSource();
        gamesList = dataSource.getAllGames();
        return gamesList;
    }

    @Override
    public void onClick(View v) {
        if(v.getTag().equals(CHANGE_LAYOUT)){
            Toast.makeText(naviDrawerActivity, "Click "+CHANGE_LAYOUT, Toast.LENGTH_SHORT).show();
            isGridLayout = !isGridLayout;
            gamesAdapter.setGridLayout(isGridLayout);
            if(isGridLayout){
                gamesCardListView.setLayoutManager(gamesCardGridLayoutManager);
            }else {
                gamesCardListView.setLayoutManager(gamesCardListLayoutManager);
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isGameListLayout", isGridLayout);
            editor.apply();
        }else if(v.getTag().equals(SORT_TITLE)){
            Toast.makeText(naviDrawerActivity, "Click "+SORT_TITLE, Toast.LENGTH_SHORT).show();
        }else if(v.getTag().equals(SORT_PLATFORM)){
            Toast.makeText(naviDrawerActivity, "Click "+SORT_PLATFORM, Toast.LENGTH_SHORT).show();
        }else if(v.getTag().equals(SORT_RATING)){
            Toast.makeText(naviDrawerActivity, "Click "+SORT_RATING, Toast.LENGTH_SHORT).show();
        }else if(v.getTag().equals(SORT_FAVOURITE)){
            Toast.makeText(naviDrawerActivity, "Click "+SORT_FAVOURITE, Toast.LENGTH_SHORT).show();
        }
        gameListActionMenu.close(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        gameListActionMenu.close(true);
        gameListActionButton.detach();
    }
}
