package com.itgarage.harvey.gamecollections.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;
import com.itgarage.harvey.gamecollections.adapters.GameListAdapter;
import com.itgarage.harvey.gamecollections.db.GamesDataSource;
import com.itgarage.harvey.gamecollections.models.Game;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends Fragment {
    static RecyclerView gamesCardListView;
    public static GameListAdapter gamesAdapter;
    static RecyclerView.LayoutManager gamesCardListLayoutManager;
    public static NaviDrawerActivity naviDrawerActivity;
    static TextView noResultTextView;
    static LinearLayout noResultLinearLayout;
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
        gamesCardListView = (RecyclerView) rootView.findViewById(R.id.gameCardList);
        //gamesCardListView.setHasFixedSize(true);
        gamesCardListLayoutManager = new LinearLayoutManager(naviDrawerActivity.getContext());
        gamesCardListView.setLayoutManager(gamesCardListLayoutManager);
        gamesAdapter = new GameListAdapter(getGameList(), naviDrawerActivity);

        /*noResultTextView = (TextView) rootView.findViewById(R.id.textViewNoResult);
        noResultTextView.setVisibility(View.GONE);*/

        noResultLinearLayout = (LinearLayout) rootView.findViewById(R.id.noGameInDataBaseLinearLayout);

        if(gamesAdapter.getItemCount()==0){
            changeUIsWhenDataSetChange(false);
        }else {
            changeUIsWhenDataSetChange(true);
        }
        return rootView;
    }

    public static void changeUIsWhenDataSetChange(boolean hasData){

        if(!hasData){
            gamesCardListView.setVisibility(View.GONE);
            //noResultTextView.setVisibility(View.VISIBLE);
            noResultLinearLayout.setVisibility(View.VISIBLE);

        }else {
            gamesCardListView.setAdapter(gamesAdapter);
            gamesCardListView.setVisibility(View.VISIBLE);
            //noResultTextView.setVisibility(View.GONE);
            noResultLinearLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NaviDrawerActivity) activity).onSectionAttached(2);
        this.naviDrawerActivity = (NaviDrawerActivity) activity;
    }

    private List<Game> getGameList() {
        List<Game> gamesList = new ArrayList<Game>();
        GamesDataSource dataSource = naviDrawerActivity.getDataSource();
        gamesList = dataSource.getAllGames();
        return gamesList;
    }

}
